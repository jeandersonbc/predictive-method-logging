subprojects {
    apply(plugin = "java-library")
    repositories {
        mavenCentral()
    }
}

tasks.register<Exec>("fetch-apache-projects") {
    description = "Downloads Apache projects"
    group = "Subjects"
    commandLine = listOf("./tools/apache-download.sh")
}
