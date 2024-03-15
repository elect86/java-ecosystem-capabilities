plugins {
    id("groovy")
    id("gradlexbuild.java-ecosystem-capabilities-lifecycle")
    id("gradlexbuild.java-ecosystem-capabilities-documentation")
    //    id("org.gradlex.internal.plugin-publish-conventions") version "0.6"
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "org.gradlex"
version = "1.5.2.00"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(11) // to run tests that use Android with 11
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 8
}

gradlePlugin.plugins.create("${project.group}.${project.name}") {
    id = "${project.group}.${project.name}"
    displayName = "Java Ecosystem Capabilities Gradle Plugin"
    description = "Adds Capabilities to well-known Components hosted on Maven Central."
    tags = listOf("capabilities", "java",
                  "asm", "bouncycastle", "cglib", "commons-io", "dom4j", "guava", "hamcrest", "javax", "jakarta", "junit",
                  "postgresql", "stax", "velocity", "woodstox")
    implementationClass = "org.gradlex.javaecosystem.capabilities.JavaEcosystemCapabilitiesPlugin"
}

//pluginPublishConventions {
//    id("${project.group}.${project.name}")
//    implementationClass("org.gradlex.javaecosystem.capabilities.JavaEcosystemCapabilitiesPlugin")
//    displayName("Java Ecosystem Capabilities Gradle Plugin")
//    description("Adds Capabilities to well-known Components hosted on Maven Central.")
//    tags("capabilities", "java",
//         "asm", "bouncycastle", "cglib", "commons-io", "dom4j", "guava", "hamcrest", "javax", "jakarta", "junit",
//         "postgresql", "stax", "velocity", "woodstox")
//    gitHub("https://github.com/gradlex-org/java-ecosystem-capabilities")
//    website("https://github.com/gradlex-org/java-ecosystem-capabilities")
//    developer {
//        id = "britter"
//        name = "Benedikt Ritter"
//        email = "benedikt@gradlex.org"
//    }
//    developer {
//        id = "jjohannes"
//        name = "Jendrik Johannes"
//        email = "jendrik@gradlex.org"
//    }
//    developer {
//        id = "ljacomet"
//        name = "Louis Jacomet"
//        email = "louis@gradlex.org"
//    }
//}

publishing.repositories.maven {
    name = "sciJava"
    credentials(PasswordCredentials::class)
    url = uri("https://maven.scijava.org/content/repositories/releases")
}

//gradlePlugin {
//    plugins {
//        create("logging-capabilities") {
//            id = "org.gradlex.logging-capabilities"
//            implementationClass = "org.gradlex.javaecosystem.capabilities.LoggingCapabilitiesPlugin"
//            displayName = "Java Logging Capabilities"
//            description = "Adds configuration options for resolving logging framework conflicts."
//            tags = listOf("dependency", "dependencies", "dependency-management", "logging", "slf4j", "log4j2")
//        }
//        create("java-ecosystem-capabilities-base") {
//            id = "org.gradlex.java-ecosystem-capabilities-base"
//            implementationClass = "org.gradlex.javaecosystem.capabilities.JavaEcosystemCapabilitiesBasePlugin"
//       }
//    }
//}

dependencies {
    testImplementation("org.gradle.exemplar:samples-check:1.0.2")
    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

testing.suites.named<JvmTestSuite>("test") {
    useJUnitJupiter()
    listOf("6.8.3", "6.9.4", "7.0.2", "7.6.4", "8.0.2").forEach { gradleVersionUnderTest ->
        targets.register("test${gradleVersionUnderTest}") {
            testTask {
                group = LifecycleBasePlugin.VERIFICATION_GROUP
                description = "Runs tests against Gradle $gradleVersionUnderTest"
                useJUnitPlatform {
                    excludeTags("no-cross-version")
                }
                systemProperty("gradleVersionUnderTest", gradleVersionUnderTest)

                exclude("**/*SamplesTest.class") // Not yet cross-version ready
            }
        }
    }
    targets.all {
        testTask {
            maxParallelForks = 4
            inputs.dir(layout.projectDirectory.dir("samples"))
        }
    }
}

