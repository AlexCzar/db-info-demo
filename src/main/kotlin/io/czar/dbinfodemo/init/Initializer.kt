package io.czar.dbinfodemo.init

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.czar.dbinfodemo.model.PostgreSettings
import io.czar.dbinfodemo.model.UserAccount
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean
import org.springframework.data.repository.init.Jackson2ResourceReader
import org.springframework.util.ClassUtils
import java.io.IOException
import java.util.*


@Configuration
@Profile("dev")
class Initializer {
	@Bean
	fun repositoryPopulator() =
			CustomRepositoryPopulatorFactoryBean().apply {
				setResources(arrayOf(ClassPathResource("test-data.json")))
			}
}

class CustomRepositoryPopulatorFactoryBean : Jackson2RepositoryPopulatorFactoryBean() {

	override fun getResourceReader() = CustomJackson2ResourceReader(DEFAULT_MAPPER)

	companion object {
		private val DEFAULT_MAPPER = ObjectMapper().apply {
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		}
	}
}

class CustomJackson2ResourceReader(private val mapper: ObjectMapper) : Jackson2ResourceReader(mapper) {
	private val typeKey = "_class"

	override fun readFrom(resource: Resource, classLoader: ClassLoader?): Any {
		val stream = resource.inputStream
		val node = mapper.readerFor(JsonNode::class.java).readTree(stream)
		if (node.isArray) {
			val elements = node.elements()
			val result = ArrayList<Any>()
			while (elements.hasNext()) {
				val element = elements.next()
				result.add(readSingle(element, classLoader))
			}
			return result
		}

		return readSingle(node, classLoader)
	}

	@Throws(IOException::class)
	private fun readSingle(node: JsonNode, classLoader: ClassLoader?): Any {
		val typeName = checkNotNull(node.findValue(typeKey).asText())
		val type = ClassUtils.resolveClassName(typeName, classLoader)
		var obj: Any = mapper.readerFor(type).readValue(node)
		obj = if (obj is DataSet) getData(obj) else obj
		return obj
	}
}

fun getData(set: DataSet) = set.users.toMutableList<Any>().apply { addAll(set.configurations) }

class DataSet(
		val users: Collection<UserAccount> = listOf(),
		val configurations: Collection<PostgreSettings> = listOf()
)
