plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "projects"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // БАЗОВЫЕ - без этих ничего не заработает
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // БАЗА ДАННЫХ
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    // RABBITMQ
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // ДОПОЛНИТЕЛЬНО
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Для разработки
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ТЕСТИРОВАНИЕ - МИНИМУМ
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("encrypt-it.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = true
}

tasks.jar {
    enabled = false
}