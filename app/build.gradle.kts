plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    namespace = "com.nexora.utility"
    compileSdk = 35
    val ownerAdmobAppId = System.getenv("NEXORA_ADMOB_APP_ID")?.takeIf { it.isNotBlank() }
    val ownerBannerUnitId = System.getenv("NEXORA_ADMOB_BANNER_ID")?.takeIf { it.isNotBlank() }
    val useProductionAds = ownerAdmobAppId != null && ownerBannerUnitId != null
    val resolvedAdmobAppId = if (useProductionAds) ownerAdmobAppId!! else "ca-app-pub-3940256099942544~3347511713"
    val resolvedBannerUnitId = if (useProductionAds) ownerBannerUnitId!! else "ca-app-pub-3940256099942544/9214589741"
    defaultConfig {
        applicationId = "com.nexora.utility"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        manifestPlaceholders["ADMOB_APP_ID"] = resolvedAdmobAppId
        buildConfigField("String", "ADMOB_BANNER_UNIT_ID", "\"$resolvedBannerUnitId\"")
    }
    val releaseKeystorePath = System.getenv("NEXORA_KEYSTORE_PATH")
    val releaseStorePassword = System.getenv("NEXORA_KEYSTORE_PASSWORD")
    val releaseKeyAlias = System.getenv("NEXORA_KEY_ALIAS")
    val releaseKeyPassword = System.getenv("NEXORA_KEY_PASSWORD")
    signingConfigs {
        create("release") {
            if (!releaseKeystorePath.isNullOrBlank() &&
                !releaseStorePassword.isNullOrBlank() &&
                !releaseKeyAlias.isNullOrBlank() &&
                !releaseKeyPassword.isNullOrBlank()
            ) {
                storeFile = file(releaseKeystorePath)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    buildFeatures {
        compose = true
        aidl = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "17" }
    buildTypes {
        getByName("release") {
            if (!releaseKeystorePath.isNullOrBlank() &&
                !releaseStorePassword.isNullOrBlank() &&
                !releaseKeyAlias.isNullOrBlank() &&
                !releaseKeyPassword.isNullOrBlank()
            ) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.biometric)
    implementation(libs.google.mobile.ads)
    implementation(libs.google.ump)
    implementation(libs.wireguard.tunnel)
    coreLibraryDesugaring(libs.desugar.jdk)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
