pipeline {
    agent any
    triggers {
        cron('* * * * *') // every minute
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t job-app .'
            }
        }
        stage('Run Latest Build') {
            steps {
                script {
                    def result = sh(script: "docker run --rm job-app", returnStdout: true).trim()
                    env.JOB_RESULT = result
                }
            }
        }
    }
    post {
        always {
            emailext(
                subject: "Latest Stable Build Result",
                body: "${env.JOB_RESULT}",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
