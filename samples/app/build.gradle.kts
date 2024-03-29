plugins {
    id("sample.conventions")
}

android {
    namespace = "com.niusounds.wdlsample"
    defaultConfig {
        applicationId = "com.niusounds.wdlsample"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.accompanist.permissions)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.lifecycle.runtime)
    implementation(libs.compose.activity)
    debugImplementation(libs.compose.ui.tooling)
}
