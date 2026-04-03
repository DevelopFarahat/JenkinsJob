pipeline {
    agent any

    environment {
        API_RESULT = ""
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Verify JAR') {
            steps {
                script {
                    echo "Checking JAR file..."
                    sh 'ls -lh build/libs/'

                    if (!fileExists('build/libs/jenkins_job-0.0.1-SNAPSHOT.jar')) {
                        error "JAR file not found!"
                    }

                    echo "✅ Using JAR: build/libs/jenkins_job-0.0.1-SNAPSHOT.jar"
                }
            }
        }

        stage('Run Daily API Job') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {

                        def output = sh(
                            script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall --spring.main.web-application-type=none',
                            returnStdout: true
                        ).trim()

                        echo "Full Output:\n${output}"

                        // Extract only JSON line (ignore Spring logs)
                        def jsonLine = output.readLines().find { it.startsWith('[') || it.startsWith('{') }

                        echo "Extracted JSON:\n${jsonLine}"

                        env.API_RESULT = jsonLine ?: "EMPTY"

                        // If API returned data → mark UNSTABLE
                        if (jsonLine && jsonLine != "[]" && jsonLine != "{}") {
                            currentBuild.result = 'UNSTABLE'
                            echo "⚠️ Build marked as UNSTABLE بسبب وجود بيانات في API"
                        }
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def output = sh(
                        script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall2 --spring.main.web-application-type=none',
                        returnStdout: true
                    ).trim()

                    echo "Response (dailyApiCall2):\n${output}"
                }
            }
        }

        stage('Run Daily API Job 3') {
            steps {
                script {
                    def output = sh(
                        script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall3 --spring.main.web-application-type=none',
                        returnStdout: true
                    ).trim()

                    echo "Response (dailyApiCall3):\n${output}"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "Final API_RESULT: ${env.API_RESULT}"

                emailext(
                    subject: "Pipeline Status: ${currentBuild.currentResult}",
                    body: """
Build Status: ${currentBuild.currentResult}

API Result (dailyApiCall):
${env.API_RESULT}
""",
                    to: 'your-email@example.com'
                )
            }
        }
    }
}