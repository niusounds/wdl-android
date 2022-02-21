plugins {
    id("sample.conventions")
}

android {
    defaultConfig {
        applicationId = "com.niusounds.wdlsample"
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.junit)
    androidTestImplementation(libs.android.test)
    androidTestImplementation(libs.espresso)
}