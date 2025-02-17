plugins {
    alias(libs.plugins.spotless)
    id("jacoco-report-aggregation")
    alias(libs.plugins.sonarqube)
}

group = "com.github.mylie-project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

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

subprojects {
    group = rootProject.group
    version = rootProject.version
    repositories {
        mavenCentral()
    }

    afterEvaluate {
        if(project.hasProperty("java-library")||project.hasProperty("java")){
            apply(plugin = libs.plugins.spotless.get().pluginId)
            apply(plugin = "jacoco")

            tasks.withType(JacocoReport::class.java).all {
                dependsOn(tasks.findByName("test"))
                reports {
                    xml.required.set(true)
                }
            }

            tasks.withType<Test>().configureEach {
                finalizedBy(tasks.withType<JacocoReport>())
            }

            dependencies {
                val implementation by configurations
                val compileOnly by configurations
                val annotationProcessor by configurations
                val api by configurations
                api(libs.logging.api)
                compileOnly(libs.lombok)
                annotationProcessor(libs.lombok)
            }

            spotless{
                java{
                    removeUnusedImports()
                    importOrder()
                    eclipse().configFile(rootProject.file("javaFormat.xml"))
                    formatAnnotations()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }
    }
}