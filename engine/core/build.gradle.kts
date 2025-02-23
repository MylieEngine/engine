plugins {
    id("java-library")
}


dependencies {
    api(project(":utils"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
}

tasks{
    test{
        useJUnitPlatform()
    }
}