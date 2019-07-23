import com.lightningkite.konvenience.gradle.*
import java.util.Properties

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

buildscript {
    val versions = java.util.Properties().apply {
        load(project.file("versions.properties").inputStream())
    }
    repositories {
        mavenLocal()
        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    }
    dependencies {
        classpath("com.lightningkite:konvenience:+")
        classpath("com.lightningkite:mirror-plugin:${versions.getProperty("mirror")}")
    }
}
apply(plugin = "com.lightningkite.mirror")
apply(plugin = "com.lightningkite.konvenience")


repositories {
    mavenLocal()
    mavenCentral()
    maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://kotlin.bintray.com/ktor")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
}

val versions = Properties().apply {
    load(project.file("versions.properties").inputStream())
}

group = "com.lightningkite"
version = versions.getProperty("mirror")

kotlin {
    sources(tryTargets = setOf(KTarget.jvm)) {
        main {
            dependency(standardLibrary)
            dependency(coroutines(versions.getProperty("kotlinx_coroutines")))
            dependency(serialization(versions.getProperty("kotlinx_serialization")).type(KDependencyType.Api))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "kommon", versions.getProperty("kommon")))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "mirror-runtime", versions.getProperty("mirror")))
            dependency(projectOrMavenDashPlatform("com.lightningkite", "mirror-ktor", versions.getProperty("mirror")))
        }
        test {
            dependency(testing)
            dependency(testingAnnotations)
            dependency(projectOrMavenDashPlatform("com.lightningkite", "recktangle", versions.getProperty("recktangle")))
        }

        KTarget.jvm.sources {
            main {
                dependencies {
                    api("io.ktor:ktor-server-core:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-auth:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-websockets:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-metrics:${versions.getProperty("ktor")}")
                }
            }
            test {
                dependencies {
                    api("io.ktor:ktor-server-netty:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-server-cio:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-auth-jwt:${versions.getProperty("ktor")}")
                    api("io.ktor:ktor-server-test-host:${versions.getProperty("ktor")}")
                }
            }
        }
    }
    jvm {
        compilations.getByName("main") {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}

publishing {
    doNotPublishMetadata()
    repositories {
        bintray(
                project = project,
                organization = "lightningkite",
                repository = "com.lightningkite.krosslin"
        )
    }

    appendToPoms {
        github("lightningkite", "mirror-kotlin")
        licenseMIT()
        developers {
            developer {
                id.set("UnknownJoe796")
                name.set("Joseph Ivie")
                email.set("joseph@lightningkite.com")
                timezone.set("America/Denver")
                roles.set(listOf("architect", "developer"))
                organization.set("Lightning Kite")
                organizationUrl.set("http://www.lightningkite.com")
            }
        }
    }
}


////import java.nio.file.Files
//import com.lightningkite.konvenience.gradle.*
//import java.util.Properties
//
//plugins {
//    kotlin("jvm") version "1.3.21"
//    `maven-publish`
//}
//
//buildscript {
//    val versions = java.util.Properties().apply {
//        load(project.file("versions.properties").inputStream())
//    }
//    repositories {
//        mavenLocal()
//        maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
//    }
//    dependencies {
//        classpath("com.lightningkite:konvenience:+")
//        classpath("com.lightningkite:mirror-plugin:${versions.getProperty("mirror")}")
//    }
//}
//apply(plugin = "com.lightningkite.mirror")
//apply(plugin = "com.lightningkite.konvenience")
//
//
//repositories {
//    mavenLocal()
//    mavenCentral()
//    maven("https://dl.bintray.com/lightningkite/com.lightningkite.krosslin")
//    maven("https://kotlin.bintray.com/ktor")
//    maven("https://kotlin.bintray.com/kotlinx")
//}
//
//val versions = Properties().apply {
//    load(project.file("versions.properties").inputStream())
//}
//
//group = "com.lightningkite"
//version = versions.getProperty("mirror")
//
//dependencies {
//    implementation(kotlin("stdlib"))
////    compile("com.lightningkite:mirror-runtime:${versions.getProperty("mirror")}")
//    implementation(project(":kommon"))
//    implementation(project(":mirror-runtime"))
//    implementation(project(":mirror-ktor"))
//    implementation("io.ktor:ktor-server-core:${versions.getProperty("ktor")}")
//    implementation("io.ktor:ktor-auth:${versions.getProperty("ktor")}")
//    implementation("io.ktor:ktor-websockets:${versions.getProperty("ktor")}")
//    testApi("io.ktor:ktor-server-netty:${versions.getProperty("ktor")}")
//    testApi("io.ktor:ktor-server-cio:${versions.getProperty("ktor")}")
//    testApi("io.ktor:ktor-auth-jwt:${versions.getProperty("ktor")}")
//    testApi("io.ktor:ktor-server-test-host:${versions.getProperty("ktor")}")
//}
//
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//    doNotPublishMetadata()
//    repositories {
//        bintray(
//                project = project,
//                organization = "lightningkite",
//                repository = "com.lightningkite.krosslin"
//        )
//    }
//
//    appendToPoms {
//        github("lightningkite", "mirror-kotlin")
//        licenseMIT()
//        developers {
//            developer {
//                id.set("UnknownJoe796")
//                name.set("Joseph Ivie")
//                email.set("joseph@lightningkite.com")
//                timezone.set("America/Denver")
//                roles.set(listOf("architect", "developer"))
//                organization.set("Lightning Kite")
//                organizationUrl.set("http://www.lightningkite.com")
//            }
//        }
//    }
//}
