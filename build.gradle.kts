import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		maven(url = "https://repo.spring.io/libs-milestone")
	}

	dependencies {
		// TODO: move to `plugins` block as soon as 2.0.0.RELEASE comes out
		classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.0.M5")
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
	gradleVersion = "4.3"
	distributionType = Wrapper.DistributionType.ALL
}

repositories {
	jcenter()
	maven(url = "https://repo.spring.io/libs-milestone")
}
