plugins {
    id("java-library")
}


dependencies {
    api(project(":utils"))
    api(project(":core"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
}

tasks{
    test{
        useJUnitPlatform()
    }
}