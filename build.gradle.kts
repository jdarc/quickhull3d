import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    `java-library`
    `maven-publish`
}

group = "com.zynaps"
version = "1.0.0"

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name, "Implementation-Version" to project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("quickhull3d") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set(rootProject.name)
                description.set("A Robust 3D Convex Hull Algorithm in Kotlin")
                url.set("https://github.com/jdarc/quickhull3d")
                licenses {
                    license {
                        name.set("BSD 2-Clause License")
                        url.set("https://opensource.org/licenses/BSD-2-Clause")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("jdarc")
                        name.set("Jean d'Arc")
                    }
                }
                scm {
                    url.set("https://github.com/jdarc/quickhull3d")

                }
            }
        }
    }
}
