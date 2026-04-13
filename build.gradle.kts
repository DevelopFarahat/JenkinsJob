plugins {
    id("java")
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.jenkins.job"
version = "0.0.1-SNAPSHOT"
description = "FirstJenkinsJob"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register<JavaExec>("dailyApiCall") {
    group = "application"
    description = "Run the Daily API Call task directly"

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.jenkins.job.firstjenkinsjob.service.DailyApiTask")
}
