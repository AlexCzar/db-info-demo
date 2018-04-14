import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import Versions
import Deps
import springBoot

plugins {
	java
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	springBoot
	springDependencyManagement
}

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
	gradleVersion = Versions.gradle
	distributionType = Wrapper.DistributionType.ALL
}

repositories {
	jcenter()
	maven(url = "https://repo.spring.io/libs-milestone")
}

/*
 * This should seem a bit over engineered for this simple project but it is an experiment
 * that will probably be migrated to a much larger and complex production project after
 * some playing with it.
 */
dependencies {
	infix fun List<*>.addTo(configuration: (Any) -> Dependency) {
		forEach {
			when (it) {
				is List<*> -> it.addTo(configuration)
				is Any -> configuration(it)
			}
		}
	}

	listOf(
		kotlin("stdlib-jdk8"),
		kotlin("reflect"),
		Deps.kotlinLogging,

		Deps.springBoot("actuator"),
		Deps.springBoot("starter-web"),
		Deps.springBoot("starter-logging"),
		Deps.springBoot("starter-data-jpa"),
		Deps.springBoot("starter-security"),

		Deps.springSecurity("config"),
		Deps.springSecurity("web"),

		Deps.h2,
		Deps.postgresql,
		Deps.hibernateEntityManager,
		Deps.jjwt,
		Deps.swagger
	) addTo ::implementation


	listOf(
		Deps.springBoot("devtools")
	) addTo ::runtimeOnly


	listOf(
		Deps.springBoot("starter-test"),
		Deps.springSecurity("test"),
		Deps.jsonPathAssert
	) addTo ::testImplementation

}
