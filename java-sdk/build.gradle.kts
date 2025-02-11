import org.radarbase.gradle.plugin.radarKotlin
import org.radarbase.gradle.plugin.radarPublishing

plugins {
    id("org.radarbase.radar-root-project") version Versions.radarCommons
    id("org.radarbase.radar-dependency-management") version Versions.radarCommons
    id("org.radarbase.radar-kotlin") version Versions.radarCommons apply false
    id("org.radarbase.radar-publishing") version Versions.radarCommons apply false
    id("com.github.davidmc24.gradle.plugin.avro-base") version Versions.avroGenerator apply false
    kotlin("plugin.allopen") version Versions.kotlin apply false
    `maven-publish`
}

allprojects {
    version = "0.8.5"
    group = "uk.ac.uom.dhs"
}



radarRootProject {
    projectVersion.set(Versions.project)
    gradleVersion.set(Versions.gradle)
}

// Configuration
val githubRepoName = "UoM-Digital-Health-Software/CONNECT-RADAR-Schemas"
val githubUrl = "https://github.com/${githubRepoName}.git"
val githubIssueUrl = "https://github.com/$githubRepoName/issues"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.radarbase.radar-kotlin")

    repositories{
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    radarKotlin {
        javaVersion.set(Versions.java)
        kotlinVersion.set(Versions.kotlin)
        slf4jVersion.set(Versions.slf4j)
        log4j2Version.set(Versions.log4j2)
        junitVersion.set(Versions.junit)
    }

    afterEvaluate {
        configurations.all {
            exclude(group = "org.slf4j", module = "slf4j-log4j12")
        }
    }



}

// Configure applications
configure(listOf(
    project(":radar-schemas-tools"),
    project(":radar-catalog-server"),
)) {
    apply(plugin = "application")
}

// Configure libraries
configure(listOf(
    project(":radar-schemas-commons"),
    project(":radar-schemas-core"),
    project(":radar-schemas-registration")
)) {
    apply(plugin = "java-library")
    apply(plugin = "org.radarbase.radar-kotlin")
    apply(plugin = "org.radarbase.radar-publishing")

    radarKotlin {
        javaVersion.set(17)
    }

        configure<PublishingExtension> {
    repositories {
         println("helo world")

        maven {
            name = "CONNECT-RADAR-Schemas"
            url = uri("https://maven.pkg.github.com/UoM-Digital-Health-Software/CONNECT-RADAR-Schemas")
            credentials {
                username = "jindrich.gorner@manchester.ac.uk"
                password = ""
               println("GitHubPackages build.gradle\n\tusername=$username\n\ttoken=$password")

            }
        }
    }
        publications {

            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }

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
