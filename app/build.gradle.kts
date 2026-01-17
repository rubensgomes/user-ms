/*
 * Copyright 2025 Rubens Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the__LICENSE] [1].
 */

/**
 * This is a blueprint Gradle build.gradle.kts file used by Rubens Gomes during the creation of a
 * new Gradle Spring Boot Java development project.
 *
 * @author [Rubens Gomes](https://rubensgomes.com)
 */
plugins {
    id("idea")
    id("jacoco")
    id("java")
    id("version-catalog")
    alias(libs.plugins.release)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.task.tree)
}

// --------------- >>> dependencies <<< ---------------------------------------

dependencies {
    // Import the Spring Boot 4 BOM
    implementation(platform(libs.spring.boot.bom))
    testImplementation(platform(libs.spring.boot.bom))

    // ########## compileOnly ##################################################
    compileOnly("org.projectlombok:lombok")

    // ########## implementation ###############################################
    // spring boot starter dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Security and JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    // io.jsonwebtoken:jjwt-api
    implementation(libs.jjwt.api)

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // API Documentation
    // org.springdoc:springdoc-openapi-starter-webmvc-ui
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // Email
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // ########## developmentOnly ##############################################
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ########## annotationProcessor ########################################
    annotationProcessor("org.projectlombok:lombok")

    // ########## runtimeOnly ##################################################
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    // io.jsonwebtoken:jjwt-impl
    runtimeOnly(libs.jjwt.impl)
    // io.jsonwebtoken:jjwt-jackson
    runtimeOnly(libs.jjwt.jackson)
    runtimeOnly("com.h2database:h2")

    // ########## testImplementation ###########################################
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // ########## testRuntimeOnly #############################################
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle IDEA Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Idea plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/idea_plugin.html

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Java Plugin <<< ---------------------------------
// NOTE: This section is dedicated to configuring the Java plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/java_plugin.html

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor.set(JvmVendorSpec.AMAZON)
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to project.properties["title"],
                "Implementation-Title" to project.properties["artifactId"],
                "Implementation-Version" to project.properties["version"],
                "Implementation-Vendor" to project.properties["developerName"],
                "Built-By" to project.properties["developerId"],
                "Build-Jdk" to System.getProperty("java.home"),
                "Created-By" to
                    "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            ),
        )
    }
}

tasks.compileJava {
    // Ensure we have a clean code prior to compilateion
    dependsOn("spotlessApply")
}

tasks.javadoc {
    // Exclude Kotlin files from Javadoc since Javadoc can't process them
    exclude("**/*.kt")
    source = sourceSets.main.get().allJava

    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.named<Test>("test") { useJUnitPlatform() }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle jaCoCo Plugin <<< -------------------------------
// NOTE: This section is dedicated to configuring the jacoco plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jacoco_plugin.html

tasks.jacocoTestReport {
    // tests are required to run before generating the report
    dependsOn(tasks.test)
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle JVM Test Suite Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the JVM Test Suite plugin.
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    // WARNING: If a serviceability tool is in use, please run with
    // -XX:+EnableDynamicAgentLoading to hide this warning
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    // report is always generated after tests run
    finalizedBy(tasks.jacocoTestReport)
}

val licenseHeaderText =
    """
    /*
     * Copyright 2026 Rubens Gomes
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * You may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    """.trimIndent()

// ----------------------------------------------------------------------------
// --------------- >>> com.diffplug.spotless Plugin <<< -----------------------
// NOTE: This section is dedicated to configuring the spotless plugin.
// ----------------------------------------------------------------------------
// https://github.com/diffplug/spotless

spotless {
    // Java formatting
    java {
        target("src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        licenseHeader(licenseHeaderText)
        importOrder("java", "javax", "org", "com", "")
        trimTrailingWhitespace()
        endWithNewline()
    }

    // Kotlin formatting
    kotlin {
        target("src/**/*.kt")
        ktfmt()
        licenseHeader(licenseHeaderText)
        trimTrailingWhitespace()
        endWithNewline()
    }

    // JSON formatting
    json {
        target("src/**/*.json")
        jackson()
    }

    // Kotlin Gradle DSL formatting (root + submodules)
    kotlinGradle {
        target("*.gradle.kts")
        // .editorconfig for fine-grained control
        ktlint().setEditorConfigPath("$rootDir/.editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ----------------------------------------------------------------------------
// --------------- >>> net.researchgate.release Plugin <<< --------------------
// ----------------------------------------------------------------------------
// https://github.com/researchgate/gradle-release

release {
    with(git) {
        pushReleaseVersionBranch.set("release")
        requireBranch.set("main")
    }
}

// --------------- >>> constants <<< ------------------------------------------
// constant being used in this build script.
//  gradle.properties:
val sonarKey = project.findProperty("sonar.projectKey") as String
val sonarOrg = project.findProperty("sonar.organization") as String
val sonarUrl = project.findProperty("sonar.host.url") as String

// ----------------------------------------------------------------------------
// --------------- >>> org.sonarqube Plugin <<< -------------------------------
// NOTE: This section is dedicated to configuring the sonarqube plugin.
// ----------------------------------------------------------------------------
// https://docs.sonarsource.com/sonarqube-server/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/

sonar {
    properties {
        // SONAR_TOKEN must be defined as an environment variable
        property("sonar.projectKey", sonarKey)
        property("sonar.organization", sonarOrg)
        property("sonar.host.url", sonarUrl)
    }
}

// task.check includes jacocoTestReport
// tasks.sonar { dependsOn("jacocoTestReport") }
tasks.sonar { dependsOn("check") }

// ----------------------------------------------------------------------------
// --------------- >>> org.springframework.boot Plugin <<< --------------------
// NOTE: This section is dedicated to configuring the Spring Boot plugin.
// ----------------------------------------------------------------------------
// https://docs.spring.io/spring-boot/gradle-plugin/index.html

springBoot { mainClass.set("com.rubensgomes.userms.UserMsApplication") }

tasks.bootJar {
    // layered.enabled.set(false)
    layered.enabled.set(true)
    dependsOn("check")
    manifest { attributes("Start-Class" to "com.rubensgomes.userms.UserMsApplication") }
}
