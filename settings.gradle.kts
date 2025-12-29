rootProject.name = "Genius"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("ktor") {
            from("io.ktor:ktor-version-catalog:3.3.3")
        }
    }
    repositories {
        mavenCentral()
    }
}