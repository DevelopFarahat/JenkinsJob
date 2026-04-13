pipelineJob('checkJunit') {
    description('Run Gradle JUnit tests and publish results')

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
                                sh './gradlew clean test'
                            }
                        }

                        stage('Publish Results') {
                            steps {
                                junit '**/build/test-results/test/*.xml'
                                archiveArtifacts artifacts: '**/build/reports/tests/test/index.html', fingerprint: true
                            }
                        }
                    }  // <-- closes stages

                    post {
                        always {
                            echo "Pipeline finished — results archived."
                        }
                        failure {
                            echo "Pipeline failed — check test reports."
                        }
                    }
                }  // <-- closes pipeline
            """.stripIndent())
            sandbox()
        }
    }
}
