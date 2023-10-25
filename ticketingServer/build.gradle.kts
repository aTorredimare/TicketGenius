import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	kotlin("plugin.jpa") version "1.7.22"
	id("com.google.cloud.tools.jib") version "3.4.0"
	id("io.freefair.lombok") version "5.3.0"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
}

group = "it.polito.waII"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

jib {
	to{
		image = "ticketing_server"
	}
	container{
		ports = listOf("8080")
		environment = mapOf(
			"KEYCLOAK_URL" to "http://keycloak:8080",
			"TEMPO_URL" to "http://tempo:9411",
			"LOKI_URL" to "http://loki:3100"
		)
	}
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")

	//reactive extension for chat
	//implementation("org.springframework.boot:spring-boot-starter-webflux")
	//implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	//implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")

	implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("com.github.dasniko:testcontainers-keycloak:2.5.0")
	implementation("org.keycloak:keycloak-admin-client:21.1.1")
	// https://mvnrepository.com/artifact/org.keycloak/keycloak-spring-boot-starter
	implementation("org.keycloak:keycloak-spring-boot-starter:21.1.1")
	// https://mvnrepository.com/artifact/org.keycloak/keycloak-spring-boot-adapter
	implementation("org.keycloak:keycloak-spring-boot-adapter:18.0.2")
	implementation ("org.springframework.boot:spring-boot-starter-aop")
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	implementation ("io.micrometer:micrometer-registry-prometheus")
	implementation ("io.micrometer:micrometer-tracing-bridge-brave")
	implementation ("io.zipkin.reporter2:zipkin-reporter-brave")
	implementation ("com.github.loki4j:loki-logback-appender:1.4.0-rc2")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation ("org.testcontainers:junit-jupiter:1.16.3")
	testImplementation("org.testcontainers:postgresql:1.16.3")
	testImplementation("io.mockk:mockk:1.8.8")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.16.3")
	}
}


