import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		maven(url = "https://repo.spring.io/libs-milestone")
	}

	dependencies {
		// TODO: move to `plugins` block as soon as 2.0.0.RELEASE comes out
		classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.0.M6")
	}
}

plugins {
	val kotlinVersion = "1.1.60"
	java
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("io.spring.dependency-management") version "1.0.3.RELEASE"
}

apply { plugin("org.springframework.boot") }

group = "io.czar"
version = "1.0"

val kotlinVersion: String? by extra {
	buildscript.configurations["classpath"]
			.resolvedConfiguration.firstLevelModuleDependencies
			.find { it.moduleName == "kotlin-gradle-plugin" }?.moduleVersion
}
java {
	sourceCompatibility = VERSION_1_8
	targetCompatibility = VERSION_1_8
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = VERSION_1_8.toString()
}

task<Wrapper>("wrapper") {
	gradleVersion = "4.3.1"
	distributionType = Wrapper.DistributionType.ALL
}

repositories {
	jcenter()
	maven(url = "https://repo.spring.io/libs-milestone")
}

dependencies {
	listOf(
//			kotlin("stdlib", kotlinVersion),
//			kotlin("stdlib-jre7", kotlinVersion),
			kotlin("stdlib-jre8", kotlinVersion),
			kotlin("reflect", kotlinVersion),

			springBoot("actuator"),
			springBoot("starter-logging"),
			springBoot("starter-security"),
			springBoot("starter-web"),

			"org.springframework.data:spring-data-jpa",
			"org.springframework:spring-jdbc",
			"org.hibernate:hibernate-entitymanager",
			"com.h2database:h2",
			"com.zaxxer:HikariCP",

			springSecurity("config"),
			springSecurity("web"),
			"io.jsonwebtoken:jjwt:0.9.0",
			"org.postgresql:postgresql"
	).forEach { implementation(it) }

	runtimeOnly(springBoot("devtools"))

	listOf(
			springBoot("starter-test"),
			springSecurity("test"),
			"com.jayway.jsonpath:json-path-assert"
	).forEach { testImplementation(it) }
}

fun springBoot(module: String, version: String = "") = "org.springframework.boot:spring-boot-$module:$version"
fun springFramework(module: String, version: String = "") = "org.springframework:spring-$module:$version"
fun springSecurity(module: String, version: String = "") = "org.springframework.security:spring-security-$module:$version"