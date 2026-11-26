plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.junit)
}

tasks.test {
    useJUnit()
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt.yml"))
}

val verifyNoFloatingPointMoney by tasks.registering {
    group = "verification"
    description = "Fails when Float or Double types appear in money/allocation domain sources."
    val protectedSources = fileTree("src/main/kotlin/com/zero/amaraty/domain/money") +
        fileTree("src/main/kotlin/com/zero/amaraty/domain/allocation")
    inputs.files(protectedSources)
    doLast {
        val forbidden = Regex("\\b(Float|Double)\\b")
        val offenders = protectedSources.files.flatMap { file ->
            file.readLines().mapIndexedNotNull { index, line ->
                if (forbidden.containsMatchIn(line)) "${file.relativeTo(projectDir)}:${index + 1}: $line" else null
            }
        }
        check(offenders.isEmpty()) {
            "Floating-point types are forbidden in money/allocation sources:\\n${offenders.joinToString("\\n")}"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(verifyNoFloatingPointMoney)
}

tasks.check {
    dependsOn(verifyNoFloatingPointMoney)
}
