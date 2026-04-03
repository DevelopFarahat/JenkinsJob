pipelineJob('Daily-API-Job-3') {
    description('Gradle build and run dailyApiCall3')

    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') { steps { checkout scm } }
                        stage('Build & Test') { steps { sh './gradlew clean build test' } }
                        stage('Run Daily API Job 3') {
                            steps {
                                script {
                                    def jarFile = "build/libs/FirstJenkinsJob-0.0.1-SNAPSHOT.jar"
                                    def response3 = sh(
                                        script: "java -jar \${jarFile} --job.name=dailyApiCall3 --spring.main.web-application-type=none",
                                        returnStdout: true
                                    ).trim()
                                    echo "Daily API Call 3 response:\\n\${response3}"
                                }
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox()
        }
    }
}
