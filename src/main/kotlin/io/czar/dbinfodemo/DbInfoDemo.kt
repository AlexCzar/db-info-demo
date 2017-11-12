package io.czar.dbinfodemo

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


fun main(args: Array<String>) {
	runApplication<DbInfoDemo>(*args)
}

@SpringBootApplication
class DbInfoDemo

@Configuration
class WebConfig : WebMvcConfigurer {
	@Autowired
	lateinit var userAccountArgumentResolver: UserAccountArgumentResolver

	override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
		argumentResolvers.add(userAccountArgumentResolver)
	}
}

@Service
class UserAccountArgumentResolver(
		val userAccountRepository: UserAccountRepository
) : HandlerMethodArgumentResolver {
	override fun supportsParameter(parameter: MethodParameter) =
			parameter.parameterType == UserAccount::class.java

	@Transactional(readOnly = true)
	override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory): Any? {
		val username = webRequest.userPrincipal?.name ?: return null
		return userAccountRepository.findByUsernameLowerCased(username.toLowerCase())
	}
}
