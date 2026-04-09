import org.radarbase.gradle.plugin.radarKotlin
import org.radarbase.gradle.plugin.radarPublishing

plugins {
    alias(libs.plugins.radar.root.project)
    alias(libs.plugins.radar.dependency.management)
    alias(libs.plugins.radar.kotlin) apply false
    alias(libs.plugins.radar.publishing) apply false
    alias(libs.plugins.avro.base) apply false
    alias(libs.plugins.kotlin.allopen) apply false
    `maven-publish`
}

allprojects {
    version = "0.8.11"
    group = "uk.ac.uom.dhs"
}



radarRootProject {
    projectVersion.set(libs.versions.project)
    gradleVersion.set(libs.versions.gradle)
}

// Configuration
val githubRepoName = "UoM-Digital-Health-Software/CONNECT-RADAR-Schemas"
val githubUrl = "https://github.com/${githubRepoName}.git"
val githubIssueUrl = "https://github.com/$githubRepoName/issues"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.radarbase.radar-kotlin")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    radarKotlin {
        log4j2Version.set(rootProject.libs.versions.log4j2)
        sentryEnabled.set(false)
    }

    // --- Vulnerability fixes start ---
    dependencies {
        constraints {
            add("implementation", rootProject.libs.jackson.bom) {
                because("Force safe version of Jackson across all modules")
            }
            add("implementation", rootProject.libs.apache.commons.lang) {
                because("Force safe version of commons-lang across all modules")
            }
        }
    }

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            // Substitute the old group/module with the new one
            substitute(module("org.lz4:lz4-java"))
                .using(module(rootProject.libs.lz4.get().toString()))
                .because("Force safe version of LZ4 across all modules")
        }
    }
    // --- Vulnerability fixes end ---

}

configure(
    listOf(
        project(":radar-schemas-tools"),
        project(":radar-catalog-server"),
    ),
) {
    apply(plugin = "application")

    radarKotlin {
        log4j2Version.set(rootProject.libs.versions.log4j2)
        sentryEnabled.set(true)
    }
}

configure(
    listOf(
        project(":radar-schemas-commons"),
        project(":radar-schemas-core"),
        project(":radar-schemas-registration"),
    ),
) {
    apply(plugin = "java-library")
    apply(plugin = "org.radarbase.radar-publishing")

    radarPublishing {
        githubUrl.set("https://github.com/$githubRepoName")
        developers {
            developer {
                id.set("jindrichgorner")
                name.set("Jindrich Gorner")
                email.set("jindrich.gorner@manchester.ac.uk")
                organization.set("The University of Manchester")
            }

        }
    }
}

// publishing {

//     repositories {
//         maven {
//             name = "CONNECT-RADAR-Schemas"
//             url = uri("https://maven.pkg.github.com/UoM-Digital-Health-Software/CONNECT-RADAR-Schemas")
//             credentials {
//                 username = "jindrich.gorner@manchester.ac.uk"
//                 password = ""
//             }
//         }
//     }
// }
