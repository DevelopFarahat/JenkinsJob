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
            steps {
                checkout scm
            }
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

        stage('Run API Jobs') {
            steps {
                script {

                    def runJob = { jobName, markUnstable = false ->

                        return {
                            echo "========== START ${jobName} =========="

                            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {

                                timeout(time: 2, unit: 'MINUTES') {

                                    retry(2) {

                                        sleep 5

                                        def response = sh(
                                            script: """
                                                java -jar ${env.JAR_FILE} \
                                                --job.name=${jobName} \
                                                --spring.main.web-application-type=none
                                            """,
                                            returnStdout: true
                                        ).trim()

                                        echo "Response (${jobName}):\n${response}"

                                        // ✅ Smart JSON extraction (markers + fallback)
                                        def jsonBlock = null

                                        if (response.contains("JSON_RESULT_START") && response.contains("JSON_RESULT_END")) {

                                            jsonBlock = response.split("JSON_RESULT_START")[1]
                                                               ?.split("JSON_RESULT_END")[0]
                                                               ?.trim()

                                        } else {
                                            def jsonLine = response.readLines().findAll {
                                                it.trim().startsWith("{") || it.trim().startsWith("[")
                                            }?.last()

                                            if (jsonLine) {
                                                jsonBlock = jsonLine.trim()
                                                echo "Fallback JSON detected: ${jsonBlock}"
                                            } else {
                                                echo "No JSON detected at all"
                                            }
                                        }

                                        def parsed = null

                                        if (jsonBlock) {
                                            try {
                                                parsed = new groovy.json.JsonSlurper().parseText(jsonBlock)
                                            } catch (Exception e) {
                                                echo "JSON parse failed: ${e.message}"
                                            }
                                        }

                                        // ✅ Mark UNSTABLE correctly
                                        if (markUnstable && parsed && (
                                                (parsed instanceof List && !parsed.isEmpty()) ||
                                                (parsed instanceof Map && !parsed.isEmpty())
                                        )) {
                                            env.API_RESULT = parsed.toString()
                                            unstable("${jobName} returned non-empty result")
                                        }
                                    }
                                }
                            }

                            echo "========== END ${jobName} =========="
                        }
                    }

                    parallel(
                        failFast: true,
                        "dailyApiCall": runJob("dailyApiCall", true),
                        "dailyApiCall2": runJob("dailyApiCall2"),
                        "dailyApiCall3": runJob("dailyApiCall3")
                    )
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
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