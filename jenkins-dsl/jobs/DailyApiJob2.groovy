pipelineJob('Daily-API-Job-2') {
    description('Gradle build and run dailyApiCall2')

    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') { steps { checkout scm } }
                        stage('Build & Test') { steps { sh './gradlew clean build test' } }
                        stage('Run Daily API Job 2') {
                            steps {
                                script {
                                    def jarFile = "build/libs/FirstJenkinsJob-0.0.1-SNAPSHOT.jar"
                                    def response2 = sh(
                                        script: "java -jar \${jarFile} --job.name=dailyApiCall2 --spring.main.web-application-type=none",
                                        returnStdout: true
                                    ).trim()
                                    echo "Daily API Call 2 response:\\n\${response2}"
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
