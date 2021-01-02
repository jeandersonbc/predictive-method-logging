subprojects {

    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }
}

tasks.register<Exec>("fetch-apache-projects") {
    description = "Downloads the FULL list of Apache projects"
    group = "Subjects"
    commandLine = listOf("./tools/apache-download.sh", "apache-projects-all.csv")
}

tasks.register<Exec>("fetch-projects-paper") {
    description = "Downloads selected Apache projects from paper"
    group = "Subjects"
    commandLine = listOf("./tools/apache-download.sh", "apache-projects-paper.csv")
}

tasks.register<Exec>("deploy-aux-tools") {
    description = "Deploy auxiliary tools"
    group = "Tools"
    dependsOn(":log-remover:distTar", ":log-placement-analyzer:distTar")
    commandLine = listOf("./tools/deploy-aux-tools.sh")
}

