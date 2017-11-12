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
	java
	groovy
	`kotlin-dsl`
	val kotlinVersion = "1.1.51"
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("io.spring.dependency-management") version "1.0.3.RELEASE"
}

apply { plugin("org.springframework.boot") }

group = "io.czar"
version = "1.0"

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
			kotlin("stdlib-jre8"),
			kotlin("reflect"),

			springBoot("actuator"),
			springBoot("starter-security"),
			springBoot("starter-web"),

			"org.springframework.data:spring-data-jpa",
			"org.springframework:spring-jdbc",
			"org.hibernate:hibernate-entitymanager",
			"com.h2database:h2",

			springSecurity("config"),
			springSecurity("web"),
			"io.jsonwebtoken:jjwt:0.9.0",
			"org.postgresql:postgresql"
	).forEach { compile(it) }

	runtime(springBoot("starter-logging"))
	runtime(springBoot("devtools"))

	listOf(
			springBoot("starter-test"),
			springSecurity("test")
	).forEach { testCompile(it) }
}

// There is an apparent bug in gradle kotlin-dsl see this issue:
// https://github.com/gradle/kotlin-dsl/issues/590
// Since it's a demo project I won't rewrite this to groovy,
// but we'll have to exclude logback.
// To execute without this, e.g. when building a final jar without running tests
// use -DkotlinDslHack=false
if (System.getProperty("kotlinDslHack", "true").toBoolean())
	configurations {
		"compile" {
			exclude(group = "ch.qos.logback", module = "logback-classic")
		}
	}

fun springBoot(module: String, version: String = "") = "org.springframework.boot:spring-boot-$module:$version"
fun springFramework(module: String, version: String = "") = "org.springframework:spring-$module:$version"
fun springSecurity(module: String, version: String = "") = "org.springframework.security:spring-security-$module:$version"
