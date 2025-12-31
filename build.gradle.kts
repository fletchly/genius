import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "io.fletchly"
version = "1.0.0-beta.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}



val paperVersion = "1.21.10-R0.1-SNAPSHOT"
val dagger_version: String by project
val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    // Paper plugin dev tools
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    paperweight.paperDevBundle(paperVersion)

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // Ktor – using the official catalog accessors
    implementation(ktor.client.core)
    implementation(ktor.client.cio)
    implementation(ktor.client.contentNegotiation)
    implementation(ktor.serialization.kotlinx.json)
    implementation(ktor.client.logging)

    implementation(libs.bundles.configurate)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    // Testing
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
    testImplementation("org.mockito:mockito-core:5.14.0")
    testImplementation("com.squareup.okhttp3:mockwebserver3:5.3.0")

    mockitoAgent("org.mockito:mockito-core:5.14.0") { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()

    // Add the javaagent early
    jvmArgs("-javaagent:${mockitoAgent.asPath}")

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.10")
    }
    jar {
        enabled = false
    }
    shadowJar {
        archiveClassifier = ""
    }
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}