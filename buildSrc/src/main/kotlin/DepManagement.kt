@file:Suppress("unused")

import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec


object Versions {
	const val gradle = "4.7-rc-2"
	const val kotlin = "1.2.31"
	const val springBoot = "2.0.1.RELEASE"
	const val springDepManagement = "1.0.5.RELEASE"
	const val kotlinLogging = "1.4.6"
	const val swaggerVersion = "2.7.0"
}

object Deps {
	const val kotlinLogging = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"
	const val hibernateEntityManager = "org.hibernate:hibernate-entitymanager"
	const val h2 = "com.h2database:h2"
	const val jjwt = "io.jsonwebtoken:jjwt:0.9.0"
	const val postgresql = "org.postgresql:postgresql"
	val swagger = listOf(
		"io.springfox:springfox-swagger2:${Versions.swaggerVersion}",
		"io.springfox:springfox-swagger-ui:${Versions.swaggerVersion}"
	)

	const val jsonPathAssert = "com.jayway.jsonpath:json-path-assert"

	fun springBoot(module: String, version: String = "") = "org.springframework.boot:spring-boot-$module:$version"
	fun springFramework(module: String, version: String = "") = "org.springframework:spring-$module:$version"
	fun springSecurity(module: String, version: String = "") =
		"org.springframework.security:spring-security-$module:$version"
}

val PluginDependenciesSpec.springBoot: PluginDependencySpec
	get() = id("org.springframework.boot") version Versions.springBoot

val PluginDependenciesSpec.springDependencyManagement: PluginDependencySpec
	get() = id("io.spring.dependency-management") version Versions.springDepManagement