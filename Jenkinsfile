pipeline {
    agent any

    environment {
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

                                        // Extract JSON safely
                                        def jsonMatch = response =~ /(\{.*\}|\[.*\])/
                                        def parsed = null

                                        if (jsonMatch) {
                                            try {
                                                parsed = new groovy.json.JsonSlurper().parseText(jsonMatch[0])
                                            } catch (Exception e) {
                                                echo "JSON parse failed: ${e.message}"
                                            }
                                        }

                                        // Special handling for main job
                                        if (markUnstable && parsed instanceof List && !parsed.isEmpty()) {
                                            currentBuild.result = 'UNSTABLE'
                                            env.API_RESULT = parsed.toString()
                                            error("${jobName} returned non-empty list")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Run jobs (parallel where possible)
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
                            <p><b>API Result:</b></p>
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
                body: "Check Jenkins logs immediately.",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}