pipeline {
    agent any

    environment {
        // ✅ Dynamically detect the JAR file
        JAR_FILE = sh(
            script: "ls build/libs/*.jar | head -n 1",
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
                // ✅ Use Gradle Wrapper instead of Maven
                sh './gradlew clean build'
            }
        }

        stage('Run API Jobs') {
            steps {
                script {

                    // ✅ Reusable function for API jobs
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

                                        // ✅ Extract JSON safely from logs
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

                                        // ✅ Mark UNSTABLE if needed (SANDBOX SAFE)
                                        if (markUnstable && parsed instanceof List && !parsed.isEmpty()) {
                                            currentBuild.result = 'UNSTABLE'
                                            env.API_RESULT = parsed.toString()   // ✅ FIXED (no JsonOutput)

                                            error("${jobName} returned non-empty list → marking UNSTABLE")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ✅ Run jobs in parallel
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
