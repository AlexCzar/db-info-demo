package io.czar.dbinfodemo

import io.czar.dbinfodemo.services.UserAccountArgumentResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


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

@Configuration
@EnableSwagger2
class SwaggerConfig {
	@Bean
	fun api(): Docket {
		return Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
	}
}
