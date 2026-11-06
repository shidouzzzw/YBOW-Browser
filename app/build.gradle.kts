import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Release signing is only configured when the release workflow provides a keystore
// through environment variables. No keystore or password is committed to this repository.
val releaseKeystorePath: String? = System.getenv("YBOW_KEYSTORE_FILE")

// The two design fonts are fetched once at build time and exposed as resources, so the
// repository holds no binary blobs while every build still bundles Figtree and Unbounded.
val designFontDir = layout.buildDirectory.dir("generated/design-fonts")
val downloadDesignFonts = tasks.register("downloadDesignFonts") {
    val outputDir = designFontDir.map { it.dir("font") }
    outputs.dir(outputDir)
    doLast {
        val target = outputDir.get().asFile
        target.mkdirs()
        listOf(
            "https://raw.githubusercontent.com/google/fonts/main/ofl/figtree/Figtree%5Bwght%5D.ttf" to "figtree_var.ttf",
            "https://raw.githubusercontent.com/google/fonts/main/ofl/unbounded/Unbounded%5Bwght%5D.ttf" to "unbounded_var.ttf",
        ).forEach { (url, name) ->
            val file = target.resolve(name)
            if (file.length() < 10_000L) {
                URI(url).toURL().openStream().use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
    }
}

android {
    namespace = "com.ybow.browser"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ybow.browser"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.5.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        if (!releaseKeystorePath.isNullOrBlank()) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = System.getenv("YBOW_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("YBOW_KEY_ALIAS")
                keyPassword = System.getenv("YBOW_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (!releaseKeystorePath.isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    sourceSets.getByName("main").res.srcDir(designFontDir)
}

tasks.named("preBuild") { dependsOn(downloadDesignFonts) }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.webkit)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
