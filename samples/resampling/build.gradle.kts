plugins {
    id("sample.conventions")
}

android {
    namespace = "com.niusounds.resampling"
    defaultConfig {
        applicationId = "com.niusounds.resampling"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.core)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.viewmodel)
    implementation(libs.livedata)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
}
