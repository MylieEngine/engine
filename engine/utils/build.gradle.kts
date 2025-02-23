plugins {
    id("java-library")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.logging.runtime)

    testImplementation(libs.mockito)
    @Suppress("UnstableApiUsage")
    mockitoAgent(libs.mockito) { isTransitive = false }
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.logging.runtime)
}

tasks{
    test{
        useJUnitPlatform()
        jvmArgs("-Xshare:off","-javaagent:${mockitoAgent.asPath}")
    }
}