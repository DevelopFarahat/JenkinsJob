pipeline {
    agent any

    environment {
        // ✅ Dynamically detect the JAR file after Gradle build
        JAR_FILE = ""
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
                // ✅ Use Gradle wrapper instead of Maven
                sh './gradlew clean build'
            }
        }

        stage('Prepare') {
            steps {
                script {
                    env.JAR_FILE = sh(
                        script: "ls build/libs/*.jar | head -n 1",
                        returnStdout: true
                    ).trim()
                    echo "Using JAR: ${env.JAR_FILE}"
                }
            }
        }

        stage('Run API Jobs') {
            steps {
                script {
                    // ✅ Reusable function for API jobs
                    def runJob = { jobName, markUnstable = false ->
                        return {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                def response = sh(
                                    script: "java -jar ${env.JAR_FILE} --job.name=${jobName} --spring.main.web-application-type=none",
                                    returnStdout: true
                                ).trim()

                                echo "Response (${jobName}):\n${response}"

                                // ✅ Extract JSON safely from logs
                                def jsonLine = response.readLines().findAll {
                                    it.trim().startsWith("{") || it.trim().startsWith("[")
                                }?.last()

                                def parsed = null
                                if (jsonLine) {
                                    try {
                                        parsed = new groovy.json.JsonSlurper().parseText(jsonLine)
                                        env.API_RESULT = groovy.json.JsonOutput.prettyPrint(jsonLine)
                                    } catch (Exception e) {
                                        echo "JSON parse failed: ${e.message}"
                                        env.API_RESULT = jsonLine
                                    }
                                } else {
                                    echo "No JSON detected in output"
                                    env.API_RESULT = response
                                }

                                // ✅ Mark UNSTABLE if needed
                                if (markUnstable && parsed instanceof List && !parsed.isEmpty()) {
                                    currentBuild.result = 'UNSTABLE'
                                    error("${jobName} returned non-empty list → marking UNSTABLE")
                                }
                            }
                        }
                    }

                    // ✅ Run jobs sequentially (to ensure env.API_RESULT persists)
                    runJob("dailyApiCall", true).call()
                    runJob("dailyApiCall2").call()
                    runJob("dailyApiCall3").call()
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
                            <pre>${env.API_RESULT ?: "No Data"}</pre>
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
                        <p>Please check Jenkins logs immediately.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
