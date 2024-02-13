plugins {
    kotlin("jvm") version "1.9.20"
    id("com.jakewharton.mosaic") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("jarDay17") {
    archiveBaseName.set("Day17")
    manifest {
        attributes["Main-Class"] = "Day17Kt"
    }
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
}
tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("jarDay18") {
    archiveBaseName.set("Day18")
    manifest {
        attributes["Main-Class"] = "Day18Kt"
    }
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
}
