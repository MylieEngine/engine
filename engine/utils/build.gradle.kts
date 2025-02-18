plugins {
    id("java-library")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.logging.runtime)
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockito)
    @Suppress("UnstableApiUsage")
    mockitoAgent(libs.mockito) { isTransitive = false }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly(libs.logging.runtime)
}

tasks{
    test{
        useJUnitPlatform()
        jvmArgs("-Xshare:off","-javaagent:${mockitoAgent.asPath}")
    }
}