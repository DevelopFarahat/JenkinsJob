pipelineJob('Daily-API-Job-1') {
    description('Gradle build and run dailyApiCall with JUnit reporting')

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
                                sh './gradlew dailyApiCall'
                                junit 'build/test-results/DailyApiTaskTest.xml'
                                script {
                                    def response = readFile('build/api-result.txt')
                                    env.API_RESULT = response ?: "No API result"
                                }
                            }
                        }
                    }

                    post {
                        failure {
                            script {
                                emailext(
                                    subject: "Daily API Job FAILED - \${currentBuild.result}",
                                    body: """<html><body>
                    <h2>Daily API Job FAILED</h2>
                                             <p>Result array:</p>
                    <pre>\${env.API_RESULT}</pre>
                                             <p>See JUnit report in Jenkins for details.</p>
                    </body></html>""",
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
