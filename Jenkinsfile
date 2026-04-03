pipeline {
    agent any

    environment {
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
                sh './gradlew clean build'
            }
        }

        stage('Prepare') {
            steps {
                script {
                    echo "Listing JAR files..."
                    sh "ls -lh build/libs/"

                    // ✅ Get all jar files (no pipes)
                    def jars = sh(
                        script: "ls build/libs/*.jar",
                        returnStdout: true
                    ).trim().split("\n")

                    // ✅ Filter in Groovy (SAFE)
                    def filtered = jars.findAll { !it.contains("plain") }

                    if (filtered.isEmpty()) {
                        error("❌ No executable JAR found in build/libs/")
                    }

                    env.JAR_FILE = filtered[0]

                    echo "✅ Selected JAR: ${env.JAR_FILE}"
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
                                    echo "⚠️ dailyApiCall returned non-empty list → UNSTABLE"
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
                        <p>Check Jenkins logs immediately.</p>
                    </body>
                </html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}