pipelineJob('Daily-API-Job-1') {
    description('Gradle build and run dailyApiCall')

    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') {
                            steps {
                                git branch: 'main',
                                    url: 'https://github.com/DevelopFarahat/JenkinsJob',
                                    credentialsId: '530aa68b-e8a0-44fc-9479-1a87b1bafb08'
                            }
                        }

                        stage('Build & Test') {
                            steps {
                                sh './gradlew clean build test'
                            }
                        }

                        stage('Run Daily API Job') {
                            steps {
                                script {
                                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                        def response = sh(
                                            script: "./gradlew dailyApiCall",
                                            returnStdout: true
                                        ).trim()
                                        echo "Daily API Call raw output:\\n\${response}"

                                        env.API_RESULT = response ?: "No API result"

                                        // If JSON parsing is needed:
                                        def parsed
                                        try {
                                            parsed = new groovy.json.JsonSlurper().parseText(response)
                                        } catch (Exception e) {
                                            parsed = null
                                        }

                                        if (parsed instanceof List && !parsed.isEmpty()) {
                                            error("Daily API Job result list is NOT empty → marking FAILURE")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    post {
                        always {
                            script {
                                emailext(
                                    subject: "Daily API Report - \${currentBuild.result}",
                                    body: "<html><body><h2>Daily API Job Result: \${currentBuild.result}</h2><pre>\${env.API_RESULT}</pre></body></html>",
                                    mimeType: 'text/html',
                                    to: "mohamed.farahat.attia@gmail.com"
                                )
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox()
        }
    }
}
