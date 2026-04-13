pipelineJob('checkJunit') {
    description('Run JUnit tests and publish results')

    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') {
                            steps {
                                checkout scm
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
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox()
        }
    }
}
