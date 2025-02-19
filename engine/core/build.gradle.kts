plugins {
    id("java-library")
}


dependencies {
    api(project(":utils"))

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks{
    test{
        useJUnitPlatform()
    }
}