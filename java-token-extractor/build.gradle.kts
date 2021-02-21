plugins {
    application
}

dependencies {
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.21.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

application {
    mainClassName = "experiment.component.App"
    applicationDefaultJvmArgs = listOf("-Xmx10g", "-Xms5g")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    failFast = true
}

