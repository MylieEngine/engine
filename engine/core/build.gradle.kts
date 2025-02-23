plugins {
    id("java-library")
}


dependencies {
    api(project(":utils"))

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks{
    test{
        useJUnitPlatform()
    }
}