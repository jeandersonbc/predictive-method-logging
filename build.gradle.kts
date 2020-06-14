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

tasks.register<Exec>("deploy-aux-tools") {
    description = "Deploy auxiliary tools"
    group = "Tools"
    dependsOn(":log-remover:distTar", ":log-placement-analyzer:distTar")
    commandLine = listOf("./tools/deploy-aux-tools.sh")
}

