pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        API_RESULT = ""
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn -B -T 1C clean verify'
            }
        }

        stage('Prepare') {
            steps {
                script {
                    env.JAR_FILE = sh(
                        script: "ls target/*.jar | head -n 1",
                        returnStdout: true
                    ).trim()
                    echo "Using JAR: ${env.JAR_FILE}"
                }
            }
        }

        stage('Run Daily API Job') {
            steps {
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        def response = sh(
                            script: "java -jar ${env.JAR_FILE} --job.name=dailyApiCall --spring.main.web-application-type=none",
                            returnStdout: true
                        ).trim()

                        echo "Response (dailyApiCall):\n${response}"

                        // Extract JSON line (last line with { or [)
                        def jsonLine = response.readLines().findAll {
                            it.trim().startsWith("{") || it.trim().startsWith("[")
                        }?.last()

                        if (jsonLine) {
                            try {
                                def parsed = new groovy.json.JsonSlurper().parseText(jsonLine)
                                // Always store as string
                                env.API_RESULT = groovy.json.JsonOutput.prettyPrint(jsonLine)

                                // Mark UNSTABLE if result is NOT empty
                                if ((parsed instanceof List && !parsed.isEmpty()) ||
                                    (parsed instanceof Map && !parsed.isEmpty())) {
                                    unstable("dailyApiCall returned non-empty result")
                                }
                            } catch (Exception e) {
                                echo "JSON parse failed: ${e.message}"
                                env.API_RESULT = jsonLine   // fallback raw JSON string
                            }
                        } else {
                            env.API_RESULT = response     // fallback raw response string
                        }

                        // Ensure API_RESULT is never null
                        if (!env.API_RESULT?.trim()) {
                            env.API_RESULT = "No API result captured"
                        }
                    }
                }
            }
        }

        stage('Run Daily API Job 2') {
            steps {
                script {
                    def response2 = sh(
                        script: "java -jar ${env.JAR_FILE} --job.name=dailyApiCall2 --spring.main.web-application-type=none",
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
                        script: "java -jar ${env.JAR_FILE} --job.name=dailyApiCall3 --spring.main.web-application-type=none",
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
                            <p><b>Build URL:</b>
                                <a href="${env.BUILD_URL}">${env.BUILD_URL}</a>
                            </p>
                            <p><b>Time:</b> ${new Date()}</p>
                            <p><b>API Result (dailyApiCall):</b></p>
                            <pre>${env.API_RESULT}</pre>
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
                        <p><b>Build URL:</b>
                            <a href="${env.BUILD_URL}">${env.BUILD_URL}</a>
                        </p>
                        <p><b>Time:</b> ${new Date()}</p>
                        <p>Please check Jenkins logs immediately.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
