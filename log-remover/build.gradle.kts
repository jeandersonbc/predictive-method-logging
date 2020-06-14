plugins {
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "nl.tudelft.serg.Main"
}

dependencies {
    implementation("com.github.javaparser:javaparser-core:3.16.1")
    implementation(project(":log-identifier"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.2")
}

tasks.test {
    useJUnitPlatform()
    failFast = true
}