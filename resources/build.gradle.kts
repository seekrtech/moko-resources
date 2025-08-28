/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("android-base-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("multiplatform-android-publish-convention")
    id("apple-main-convention")
    id("apple-bundle-searcher-convention")
    id("detekt-convention")
    id("javadoc-stub-convention")
    id("publication-convention")
}

kotlin {
    // Configure only iOS and Android targets
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting
        
        // Note: iosMain and iosTest are created by apple-main-convention plugin
        // We just need to configure the target dependencies
        val iosX64Main by getting
        val iosArm64Main by getting  
        val iosSimulatorArm64Main by getting
        
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
    }
    
    jvmToolchain(17)
}

android {
    namespace = "dev.icerock.moko.resources"
}

dependencies {
    commonMainApi(libs.mokoGraphics)
    androidMainImplementation(libs.appCompatResources)
    iosTestImplementation(libs.mokoTestCore)
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("resources-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}

val copyIosX64TestResources = tasks.register<Copy>("copyIosX64TestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest")
}

tasks.findByName("iosX64Test")!!.dependsOn(copyIosX64TestResources)

val copyIosArm64TestResources = tasks.register<Copy>("copyIosArm64TestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosSimulatorArm64/debugTest")
}

tasks.findByName("iosSimulatorArm64Test")!!.dependsOn(copyIosArm64TestResources)
