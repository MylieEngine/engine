plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.sonarqube)
    id("jacoco-report-aggregation")
}

group = "com.github.mylie-project"
version = "1.0-SNAPSHOT"



sonar {
    properties {
        property("sonar.projectKey", "MylieEngine_engine")
        property("sonar.organization", "mylieengine")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "$rootDir/build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml")
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Coverage reports"
    description = "Generates an aggregate report from all subprojects"
    dependsOn(subprojects.map { it.tasks.named("test") })

    additionalSourceDirs.from(subprojects.map { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    sourceDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    classDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].output })
    executionData.from(subprojects.map { it.tasks.named<JacocoReport>("jacocoTestReport").get().executionData })

    reports {
        xml.required.set(true)
    }
}


tasks.named("sonar"){
    dependsOn(tasks.named("jacocoRootReport"))
}

tasks.withType<Test>().configureEach {
    finalizedBy(tasks.withType<JacocoReport>())
}

dependencies {
    subprojects.forEach {
        jacocoAggregation(project(it.path))
    }
}