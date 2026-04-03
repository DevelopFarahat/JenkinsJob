pipeline {
    agent any

    environment {
        JAR_FILE = "build/libs/jenkins_job-0.0.1-SNAPSHOT.jar"
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
                    sh "ls -lh build/libs/"

                    if (!fileExists(env.JAR_FILE)) {
                        error("❌ JAR file not found: ${env.JAR_FILE}")
                    }

                    echo "✅ Using JAR: ${env.JAR_FILE}"
                }
            }
        }

        stage('Run Daily API Job') {
            steps {
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {

                        def response = sh(
                            script: "java -jar '${env.JAR_FILE}' --job.name=dailyApiCall --spring.main.web-application-type=none",
                            returnStdout: true
                        ).trim()

                        echo "Full Response:\n${response}"

                        // ✅ Extract JSON safely
                        def jsonLine = response.readLines().find {
                            it.trim().startsWith("{") || it.trim().startsWith("[")
                        }

                        if (jsonLine) {
                            try {
                                def parsed = new groovy.json.JsonSlurper().parseText(jsonLine)

                                // ✅ ALWAYS store as string
                                env.API_RESULT = groovy.json.JsonOutput.prettyPrint(
                                    groovy.json.JsonOutput.toJson(parsed)
                                )

                                // ✅ Mark UNSTABLE if list not empty
                                if (parsed instanceof List && !parsed.isEmpty()) {
                                    currentBuild.result = 'UNSTABLE'
                                    echo "⚠️ API returned non-empty list → UNSTABLE"
                                }

                            } catch (Exception e) {
                                echo "JSON parse failed: ${e.message}"
                                env.API_RESULT = jsonLine
                            }
                        } else {
                            env.API_RESULT = response ?: "No output returned"
                        }

                        // ✅ Ensure not null
                        if (!env.API_RESULT?.trim()) {
                            env.API_RESULT = "No API result captured"
                        }

                        echo "Final API_RESULT:\n${env.API_RESULT}"
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def response2 = sh(
                        script: "java -jar '${env.JAR_FILE}' --job.name=dailyApiCall2 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()

                    echo "Response (dailyApiCall2):\n${response2}"
                }
            }
        }

        stage('Run Daily API Job 3') {
            steps {
                script {
                    def response3 = sh(
                        script: "java -jar '${env.JAR_FILE}' --job.name=dailyApiCall3 --spring.main.web-application-type=none",
                        returnStdout: true
                    ).trim()

                    echo "Response (dailyApiCall3):\n${response3}"
                }
            }
        }
    }

    post {

        unstable {
            script {
                emailext(
                    subject: "⚠️ Daily API Report - UNSTABLE",
                    body: """<html>
                        <body>
                            <h2>Pipeline Status: UNSTABLE</h2>
                            <p><b>API Result (dailyApiCall):</b></p>
                            <pre>${env.API_RESULT ?: "EMPTY"}</pre>
                        </body>
                    </html>""",
                    mimeType: 'text/html',
                    to: "mohamed.farahat.attia@gmail.com"
                )
            }
        }

        failure {
            emailext(
                subject: "❌ Pipeline FAILED",
                body: """<html>
                    <body>
                        <h2>Pipeline FAILED</h2>
                        <p>Check Jenkins logs immediately.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}