plugins {
    kotlin("jvm") version "1.9.22"
}

group = "ski.mashiro"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("commons-io:commons-io:2.15.1")
    compileOnly("com.backblaze.b2:b2-sdk-core:6.1.1")
    compileOnly("com.backblaze.b2:b2-sdk-httpclient:6.1.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "ski.mashiro.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}