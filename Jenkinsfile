pipeline {
    agent any

    triggers {
        // Run daily at 9 AM
        cron('H 9 * * *')
    }

    environment {
        // Placeholder for API result
        API_RESULT = ''
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                // Build without running tests
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Daily API Job') {
            steps {
                script {
                    def response = sh(
                        script: "java -Djava.net.preferIPv4Stack=true -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()

                    echo "Captured response: ${response}"
                    env.API_RESULT = response ?: "No API result"
                }
            }
        }
    post {
        success {
            emailext(
                subject: "Daily API Report - SUCCESS",
                body: """<html>
                    <body>
                        <h2 style="color:green;">Pipeline Succeeded ✅</h2>
                        <p><b>External API response:</b></p>
                        <pre style="background:#f4f4f4; padding:10px; border:1px solid #ccc;">
${env.API_RESULT}
                        </pre>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }

        failure {
            emailext(
                subject: "Daily API Report - FAILURE",
                body: """<html>
                    <body>
                        <h2 style="color:red;">Pipeline Failed ❌</h2>
                        <p>Please check Jenkins logs for details.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
