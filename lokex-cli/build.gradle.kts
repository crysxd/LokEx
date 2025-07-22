plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    application
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.cli)
            implementation(libs.ktor.core)
            implementation(project(":lokex-core"))
        }
    }
}

distributions {
    main {
        distributionBaseName.set("lokex")
        contents {
            into("") {
                val jvmJar by tasks.getting
                from(jvmJar)
                from("src/lokex")
            }
            into("lib/") {
                val main by kotlin.jvm().compilations.getting
                from(main.runtimeDependencyFiles)
            }
            exclude("**/lokalise-exporter")
            exclude("**/lokalise-exporter.bat")
        }
    }
}

tasks.withType<Jar> {
    doFirst {
        manifest {
            val main by kotlin.jvm().compilations.getting
            attributes(
                "Main-Class" to "com.iodigital.lokex.MainKt",
                "Class-Path" to main.runtimeDependencyFiles.files.joinToString(" ") { "lib/" + it.name }
            )
        }
    }
}