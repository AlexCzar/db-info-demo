import Versions

pluginManagement {
	repositories {
		gradlePluginPortal()
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
				useVersion(Versions.kotlin)
			}
		}
	}
}
