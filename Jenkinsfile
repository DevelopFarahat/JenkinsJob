pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Test') {
            steps {
                // This is the release gating stage
                sh 'mvn clean verify'
            }
        }

        stage('Run Daily API Job') {
            steps {
                script {
                    // Wrap in catchError so the stage can fail but pipeline continues
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        def response = sh(
                            script: "java -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall --spring.main.web-application-type=none",
                            returnStdout: true
                        ).trim()

                        echo "Daily API Call raw output:\n${response}"

                        // Take the last line of output (should be JSON)
                        def lastLine = response.readLines().last()
                        env.API_RESULT = lastLine ?: "No API result"

                        // Parse JSON safely
                        def parsed
                        try {
                            parsed = new groovy.json.JsonSlurper().parseText(lastLine)
                        } catch (Exception e) {
                            echo "Could not parse JSON: ${e.message}"
                            parsed = null
                        }

                        // If parsed is an empty list, mark UNSTABLE
                        if (parsed instanceof List && parsed.isEmpty()) {
                            currentBuild.result = 'UNSTABLE'
                            error("Daily API Job result list is empty → marking UNSTABLE")
                        }
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def response2 = sh(
                        script: "java -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall2 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()
                    echo "Daily API Call 2 response:\n${response2}"
                }
            }
        }

        stage('Run Daily API Job 3') {
            steps {
                script {
                    def response3 = sh(
                        script: "java -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall3 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()
                    echo "Daily API Call 3 response:\n${response3}"
                }
            }
        }
    }

    post {
        always {
            script {
                // Email only for dailyApiCall
                emailext(
                    subject: "Daily API Report - ${currentBuild.result}",
                    body: """<html>
                        <body>
                            <h2>Daily API Job Result: ${currentBuild.result}</h2>
                            <p><b>Response (dailyApiCall):</b></p>
                            <pre>${env.API_RESULT}</pre>
                        </body>
                    </html>""",
                    mimeType: 'text/html',
                    to: "mohamed.farahat.attia@gmail.com"
                )
            }
        }
    }
}
