pipelineJob('Daily-API-Job-1') {
    description('Run daily API call and fail properly with email notification')

    definition {
        cps {
            script("""
                pipeline {
                    agent any

                    environment {
                        API_RESULT = "No API result available"
                    }

                    stages {
                        stage('Checkout') {
                            steps {
                                git branch: 'main',
                                    url: 'https://github.com/DevelopFarahat/JenkinsJob',
                                    credentialsId: '530aa68b-e8a0-44fc-9479-1a87b1bafb08'
                            }
                        }

                        stage('Run Daily API Job') {
                            steps {
                                script {
                                    sh './gradlew dailyApiCall'

                                    if (fileExists('build/api-result.txt')) {
                                        def response = readFile('build/api-result.txt').trim()
                                        env.API_RESULT = response

                                        if (!response || response == 'null') {
                                            error("API returned empty or null response")
                                        }
                                    } else {
                                        error("API result file not found")
                                    }
                                }
                            }
                        }
                    }

                    post {
                        failure {
                            emailext(
                                subject: "Daily API Job FAILED - \${currentBuild.result}",
                                body: \"\"\"<html><body>
<h2>Daily API Job FAILED</h2>
<p>Result array:</p>
<pre>\${env.API_RESULT}</pre>
</body></html>\"\"\",
                                mimeType: 'text/html',
                                to: "mohamed.farahat.attia@gmail.com"
                            )
                        }
                    }
                }
            """.stripIndent())
            sandbox()
        }
    }
}