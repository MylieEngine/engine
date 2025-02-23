import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java-library")
    alias(libs.plugins.palantirGitVersion)
}

private object Defaults {
    const val UNKNOWN = "unknown"
    const val LOCAL = "local"
    const val DATE_FORMAT_PATTERN = "dd-MM-yyyy hh:mm"
    const val VERSION_FILE_PATH = "mylie/engine/version.properties"
}


dependencies {
    api(project(":utils"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.logging.runtime)
}

tasks{
    test{
        useJUnitPlatform()
    }
}

typealias VersionDetailsClosure = groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails>

val createVersionProperties by tasks.registering(WriteProperties::class) {
    destinationFile = sourceSets.main.map {
        it.output.resourcesDir!!.resolve(Defaults.VERSION_FILE_PATH)
    }
    property("version", project.version.toString())
    val versionDetails: VersionDetailsClosure by project.extra
    addVersionDetailsProperties(versionDetails())
    property("buildTime", SimpleDateFormat(Defaults.DATE_FORMAT_PATTERN).format(Date()))
}

private fun WriteProperties.addVersionDetailsProperties(details: com.palantir.gradle.gitversion.VersionDetails) {
    property("lastTag", details.lastTag ?: Defaults.UNKNOWN)
    property("commitDistance", details.commitDistance)
    property("gitHash", details.gitHash ?: Defaults.UNKNOWN)
    property("gitHashFull", details.gitHashFull ?: Defaults.UNKNOWN)
    property("branchName", details.branchName ?: Defaults.LOCAL)
    property("isCleanTag", details.isCleanTag)
}

tasks.classes {
    dependsOn(createVersionProperties)
}

