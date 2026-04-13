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

        stage('Publish Test Results') {
            steps {
                junit '**/build/test-results/test/*.xml'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/build/reports/tests/test/*', fingerprint: true
        }
    }
}
