pipeline {
    agent any

    environment {
        API_RESULT1 = ""
        API_RESULT2 = ""
        API_RESULT3 = ""
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
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

        stage('Run Daily API Job 1') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        def output = sh(
                            script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall --spring.main.web-application-type=none',
                            returnStdout: true
                        ).trim()

                        echo "Full Output (Job1):\n${output}"

                        def jsonLine = output.readLines().reverse().find { it.trim().startsWith('[') || it.trim().startsWith('{') }
                        echo "Extracted JSON (Job1):\n${jsonLine}"

                        env.API_RESULT1 = (jsonLine ?: "EMPTY").toString()

                        if (jsonLine && jsonLine != "[]" && jsonLine != "{}") {
                            currentBuild.result = 'UNSTABLE'
                            echo "⚠️ Build marked as UNSTABLE بسبب وجود بيانات في API (Job1)"
                        }
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def output2 = sh(
                        script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall2 --spring.main.web-application-type=none',
                        returnStdout: true
                    ).trim()

                    echo "Full Output (Job2):\n${output2}"

                    def jsonLine2 = output2.readLines().reverse().find { it.trim().startsWith('[') || it.trim().startsWith('{') }
                    env.API_RESULT2 = (jsonLine2 ?: "EMPTY").toString()
                }
            }
        }

        stage('Run Daily API Job 3') {
            steps {
                script {
                    def output3 = sh(
                        script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall3 --spring.main.web-application-type=none',
                        returnStdout: true
                    ).trim()

                    echo "Full Output (Job3):\n${output3}"

                    def jsonLine3 = output3.readLines().reverse().find { it.trim().startsWith('[') || it.trim().startsWith('{') }
                    env.API_RESULT3 = (jsonLine3 ?: "EMPTY").toString()
                }
            }
        }
    }

    post {
        always {
            script {
                def emailBody = """<html>
                    <body>
                        <h2>Pipeline Status: ${currentBuild.currentResult}</h2>
                        <p><b>API Result (dailyApiCall):</b></p>
                        <pre>${env.API_RESULT1}</pre>
                        <p><b>API Result (dailyApiCall2):</b></p>
                        <pre>${env.API_RESULT2}</pre>
                        <p><b>API Result (dailyApiCall3):</b></p>
                        <pre>${env.API_RESULT3}</pre>
                    </body>
                </html>"""

                emailext(
                    subject: "Pipeline Status: ${currentBuild.currentResult}",
                    body: emailBody,
                    mimeType: 'text/html',
                    to: 'mohamed.farahat.attia@gmail.com'
                )
            }
        }
    }
}
