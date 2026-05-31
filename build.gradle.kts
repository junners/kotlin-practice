plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ind.junners"

version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(24) } }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val ktfmt by configurations.creating

ktfmt.resolutionStrategy {
    eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.3.20")
            because("ktfmt 0.62 requires Kotlin PSI/compiler 2.3.20 on its formatter classpath")
        }
    }
}

dependencies { ktfmt("com.facebook:ktfmt:0.62") }

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

fun ktfmtFiles() =
    fileTree(projectDir) {
        include("*.kts")
        include("src/**/*.kt")
        exclude("build/**")
        exclude(".gradle/**")
    }

tasks.register("klsClasspath") {
    group = "ide"
    description = "Prints the classpath used by kotlin-language-server."
    doLast {
        val classpath =
            sourceSets["main"].compileClasspath +
                sourceSets["main"].runtimeClasspath +
                sourceSets["test"].compileClasspath +
                sourceSets["test"].runtimeClasspath
        println(classpath.files.joinToString(File.pathSeparator))
    }
}

tasks.register<JavaExec>("ktfmtFormat") {
    group = "formatting"
    description = "Formats Kotlin files with ktfmt."
    classpath = ktfmt
    mainClass.set("com.facebook.ktfmt.cli.Main")
    args("--kotlinlang-style")
    args(ktfmtFiles().files.map { it.path })
}

tasks.register<JavaExec>("ktfmtCheck") {
    group = "verification"
    description = "Checks Kotlin files are formatted with ktfmt."
    classpath = ktfmt
    mainClass.set("com.facebook.ktfmt.cli.Main")
    args("--kotlinlang-style", "--dry-run", "--set-exit-if-changed")
    args(ktfmtFiles().files.map { it.path })
}

tasks.withType<Test> { useJUnitPlatform() }
