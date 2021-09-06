import org.jetbrains.kotlin.gradle.dsl.KotlinCommonToolOptions
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
    dependsOn(":scope-plugin:assemble")
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs(
            "-Xplugin=$rootDir/scope-plugin/build/libs/scope-plugin-1.0-SNAPSHOT.jar",
            "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=$buildDir"
        )
    }
}

fun KotlinCommonToolOptions.freeCompilerArgs(vararg args: String) {
    freeCompilerArgs = freeCompilerArgs + args
}
