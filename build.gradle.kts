import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json

plugins {
    kotlin("js") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

group = "io.wollinger.webgame"
version = "0.0.1"

repositories {
    mavenCentral()
}

buildscript {
    dependencies {

        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    }
}


dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

data class BuildInfo(
    val githash: String,
    val timestamp: Long
)

kotlin {
    js(IR) {
        browser {
            webpackTask {
                File("src/main/resources/build.json").apply {
                    createNewFile()
                    val bi = BuildInfo("", System.currentTimeMillis())
                    writeText(Json.encodeToString(serializer = object: SerializationStrategy<BuildInfo> {
                        override val descriptor = buildClassSerialDescriptor("BuildInfo") {
                            element<String>("githash")
                            element<Long>("timestamp")
                        }

                        override fun serialize(encoder: Encoder, value: BuildInfo) {
                            encoder.encodeStructure(descriptor) {
                                encodeStringElement(descriptor, 0, value.githash)
                                encodeLongElement(descriptor, 1, value.timestamp)
                            }
                        }

                    }, bi))
                }
                this.outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
}

