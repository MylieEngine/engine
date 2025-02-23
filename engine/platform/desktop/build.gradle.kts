plugins {
    id("java-library")
}


dependencies {
    api(project(":utils"))
    api(project(":core"))
    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks{
    test{
        useJUnitPlatform()
    }
}