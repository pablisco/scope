import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly(libs.kotlin.compiler)
    compileOnly(libs.arrow.meta)
    implementation(project(":scope-api"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.getByName<Jar>("jar") {
    from(
        zipTree(sourceSets.main.get().compileClasspath.first {
             it.absolutePath.contains("arrow-kt") && it.absolutePath.contains("arrow-meta")
        })
    )
}