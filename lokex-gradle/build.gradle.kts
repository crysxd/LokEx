plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.2.1"
    `maven-publish`
}

dependencies {
    // Use JUnit test framework for unit tests
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":lokex-core"))
}

group = "com.iodigital.lokex"

gradlePlugin {
    website = "https://github.com/iodigital-com/lokex"
    vcsUrl = "https://github.com/iodigital-com/lokex"

    val lokex by plugins.creating {
        id = "com.iodigital.lokex"
        implementationClass = "com.iodigital.lokex.LokExPlugin"
        displayName = "Gradle Lokalise Exporter"
        description = "Export strings from Lokalise using Jinja templating"
        tags = listOf("Lokalise", "export", "translations", "strings")
    }
}

// Add a source set and a task for a functional test suite
val functionalTest by sourceSets.creating
gradlePlugin.testSourceSets(functionalTest)

configurations[functionalTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())

val functionalTestTask = tasks.register<Test>("functionalTest") {
    testClassesDirs = functionalTest.output.classesDirs
    classpath =
        configurations[functionalTest.runtimeClasspathConfigurationName] + functionalTest.output
}

tasks.check {
// Run the functional tests as part of `check`
    dependsOn(functionalTestTask)
}

kotlin {
    jvmToolchain(17)
}