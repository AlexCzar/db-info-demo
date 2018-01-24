pluginManagement {
	repositories {
		gradlePluginPortal()
		maven(url = "https://repo.spring.io/libs-milestone")
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "org.springframework.boot") {
				useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
			}
			if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
				useVersion(gradle.rootProject.extra["kotlin.version"] as String)
			}
		}
	}
}
