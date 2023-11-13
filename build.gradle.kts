plugins {
    kotlin("js") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
}

group = "io.wollinger.webgame"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}


kotlin {
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChrome()
                }
            }
            webpackTask {
                this.outputFileName = "app.js"
            }
        }
        binaries.executable()
    }
}