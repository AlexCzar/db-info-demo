package io.czar.dbinfodemo

import io.czar.dbinfodemo.services.UserAccountArgumentResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
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

