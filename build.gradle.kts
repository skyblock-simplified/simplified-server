plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.sbs"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven(url = "https://central.sonatype.com/repository/maven-snapshots")
    maven(url = "https://jitpack.io")
}

dependencies {
    // Simplified Annotations
    compileOnly(libs.simplified.annotations)
    annotationProcessor(libs.simplified.annotations)
    testCompileOnly(libs.simplified.annotations)
    testAnnotationProcessor(libs.simplified.annotations)

    // Lombok Annotations

    // Tests
    testImplementation(libs.hamcrest)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.platform.launcher)

    // SpringDoc (implementation-specific, not in server-api)
    implementation(libs.springdoc.openapi.scalar) {
        exclude(group = "org.jboss.logging", module = "jboss-logging")
    }

    // Simplified infrastructure (formerly transitive via minecraft-api)
    implementation("com.github.simplified-dev:client") { version { strictly("1ca9934") } }
    implementation("com.github.simplified-dev:gson-extras") { version { strictly("f143dc1") } }
    implementation("com.github.simplified-dev:manager") { version { strictly("7063ecd") } }

    // Split minecraft-api modules
    implementation("com.github.simplified-api:skyblock") { version { strictly("f0be258") } }
    implementation("com.github.simplified-api:mojang") { version { strictly("d678198") } }
    implementation("com.github.skyblock-simplified:api") { version { strictly("488d255") } }
    implementation("com.github.simplified-api:hypixel") { version { strictly("a0f1f76") } }

    // Projects
    implementation("com.github.simplified-dev:spring-framework") { version { strictly("ac95902") } }
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    test {
        useJUnitPlatform()
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        append("META-INF/spring.factories")
        append("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")

        manifest {
            attributes["Main-Class"] = "dev.sbs.server.SimplifiedServer"
            attributes["Multi-Release"] = "true"
        }

        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }

    build {
        dependsOn(shadowJar)
    }

    register<Exec>("deploy") {
        description = "Build and deploy to remote Docker host via SSH over VPN"
        group = "deployment"
        dependsOn(shadowJar)

        doFirst {
            project.file(".env").readLines()
                .filter { it.contains('=') && !it.startsWith('#') }
                .forEach { environment(it.substringBefore('='), it.substringAfter('=')) }
        }

        commandLine("docker", "compose", "up", "-d", "--build", "--remove-orphans")
    }
}
