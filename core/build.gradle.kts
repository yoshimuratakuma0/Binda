plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("signing")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11 } }

val artifactVersion = "1.0.0"
val isSnapshot = artifactVersion.endsWith("-SNAPSHOT")
mavenPublishing {
    coordinates("com.legstart", "binda-core", artifactVersion)

    pom {
        name.set("Binda Core")
        description.set("Core abstractions for async task binding")
        inceptionYear.set("2025")
        url.set("https://github.com/yoshimuratakuma0/Binda")
        licenses {
            license {
                name.set("The Apache License, Version 2.0");
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt");
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("yoshimuratakuma0");
                name.set("Takuma Yoshimura");
                url.set("https://github.com/yoshimuratakuma0/")
            }
        }
        scm {
            url.set("https://github.com/yoshimuratakuma0/Binda");
            connection.set("scm:git:git://github.com/yoshimuratakuma0/Binda.git");
            developerConnection.set("scm:git:ssh://git@github.com/yoshimuratakuma0/Binda.git")
        }
    }
}
