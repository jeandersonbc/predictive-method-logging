plugins {
    application
}

dependencies {
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.21.0")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("org.apache.commons:commons-text:1.8")
    implementation(project(":log-identifier"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

application {
    mainClassName = "experiment.component.App"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    failFast = true
}