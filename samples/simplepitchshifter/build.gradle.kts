plugins {
    id("sample.conventions")
}

android {
    namespace = "com.niusounds.simplepitchshifter"
    defaultConfig {
        applicationId = "com.niusounds.simplepitchshifter"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.viewmodel)
    implementation(libs.livedata)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.coroutines.core)
}
