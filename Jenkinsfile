pipeline {
    agent any

    environment {
        JAR_FILE = ""
        API_RESULT = ""
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

        stage('Prepare') {
            steps {
                script {
                    // ✅ Properly quoted find command
                    env.JAR_FILE = sh(
                        script: "find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1",
                        returnStdout: true
                    ).trim()

                    echo "Using JAR: ${env.JAR_FILE}"

                    // Debug: show all JARs produced
                    sh "ls -lh build/libs/"
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

                        def jsonLine = response.readLines().findAll {
                            it.trim().startsWith("{") || it.trim().startsWith("[")
                        }?.last()

                        if (jsonLine) {
                            try {
                                def parsed = new groovy.json.JsonSlurper().parseText(jsonLine)
                                env.API_RESULT = groovy.json.JsonOutput.prettyPrint(jsonLine)

                                if (parsed instanceof List && !parsed.isEmpty()) {
                                    currentBuild.result = 'UNSTABLE'
                                    echo "dailyApiCall returned non-empty list → marking UNSTABLE"
                                }
                            } catch (Exception e) {
                                echo "JSON parse failed: ${e.message}"
                                env.API_RESULT = jsonLine
                            }
                        } else {
                            env.API_RESULT = response
                        }

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
                        <p>Please check Jenkins logs immediately.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
