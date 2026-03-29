pipeline {
    agent any

    environment {
        // ✅ Dynamically detect jar instead of hardcoding
        JAR_FILE = sh(
            script: "ls target/*.jar | head -n 1",
            returnStdout: true
        ).trim()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Run API Jobs') {
            steps {
                script {

                    // ✅ Reusable function (DRY)
                    def runJob = { jobName, markUnstable = false ->

                        return {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {

                                timeout(time: 2, unit: 'MINUTES') {
                                    retry(2) {

                                        def response = sh(
                                            script: "java -jar ${env.JAR_FILE} --job.name=${jobName} --spring.main.web-application-type=none",
                                            returnStdout: true
                                        ).trim()

                                        echo "Response (${jobName}):\n${response}"

                                        // ✅ FIXED: robust JSON extraction
                                        def jsonLine = response.readLines().findAll {
                                            it.trim().startsWith("{") || it.trim().startsWith("[")
                                        }?.last()

                                        def parsed = null

                                        if (jsonLine) {
                                            try {
                                                parsed = new groovy.json.JsonSlurper().parseText(jsonLine)
                                            } catch (Exception e) {
                                                echo "JSON parse failed: ${e.message}"
                                            }
                                        } else {
                                            echo "No JSON detected in output"
                                        }

                                        // ✅ Restore UNSTABLE behavior correctly
                                        if (markUnstable && parsed instanceof List && !parsed.isEmpty()) {
                                            currentBuild.result = 'UNSTABLE'
                                            env.API_RESULT = groovy.json.JsonOutput.prettyPrint(
                                                groovy.json.JsonOutput.toJson(parsed)
                                            )

                                            error("${jobName} returned non-empty list → marking UNSTABLE")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ✅ Parallel execution (performance boost)
                    parallel(
                        "dailyApiCall": runJob("dailyApiCall", true),
                        "dailyApiCall2": runJob("dailyApiCall2"),
                        "dailyApiCall3": runJob("dailyApiCall3")
                    )
                }
            }
        }
    }

    post {

        // ✅ Only send email when something is wrong
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