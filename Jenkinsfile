pipeline {
    agent any

    triggers {
        // Run daily at 9 AM
        cron('H 9 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
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

                    echo "Daily API Call response: ${response}"
                    env.API_RESULT = response ?: "No API result"

                    // Fail the stage if type == dad
                    if (response.contains('"type":"dad"')) {
                        error("Daily API Call returned a dad joke, failing this stage.")
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def response2 = sh(
                        script: "java -Djava.net.preferIPv4Stack=true -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall2 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()

                    echo "Daily API Call 2 response: ${response2}"
                }
            }
        }

        stage('Run Daily API Job 3') {
            steps {
                script {
                    def response3 = sh(
                        script: "java -Djava.net.preferIPv4Stack=true -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall3 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()

                    echo "Daily API Call 3 response: ${response3}"
                }
            }
        }
    }

    post {
        success {
            script {
                // Email only for dailyApiCall
                emailext(
                    subject: "Daily API Report - SUCCESS",
                    body: """<html>
                        <body>
                            <h2 style="color:green;">Pipeline Succeeded ✅</h2>
                            <p><b>External API response (dailyApiCall):</b></p>
                            <pre style="background:#f4f4f4; padding:10px; border:1px solid #ccc;">
${env.API_RESULT}
                            </pre>
                        </body>
                    </html>""",
                    mimeType: 'text/html',
                    to: "mohamed.farahat.attia@gmail.com"
                )
            }
        }

        failure {
            script {
                emailext(
                    subject: "Daily API Report - FAILURE",
                    body: """<html>
                        <body>
                            <h2 style="color:red;">Pipeline Failed ❌</h2>
                            <p>Daily API Call failed (dad joke detected or other error). Check Jenkins logs.</p>
                        </body>
                    </html>""",
                    mimeType: 'text/html',
                    to: "mohamed.farahat.attia@gmail.com"
                )
            }
        }
    }
}
