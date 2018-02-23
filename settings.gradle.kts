pluginManagement {
	repositories {
		gradlePluginPortal()
		maven(url = "https://repo.spring.io/libs-milestone")
	}
	resolutionStrategy {
		eachPlugin {
			val kotlinVersion = gradle.rootProject.extra["kotlin.version"] as String
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
			}
			if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
				println("Using Kotlin version: $kotlinVersion for ${requested.id.id}")
				useVersion(kotlinVersion)
			}
		}
	}
}
