pipeline {
    agent any
    triggers {
        cron('* * * * *') // every minute
    }
    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the image from Dockerfile in repo
                    sh 'docker build -t job-app .'
                }
            }
        }
        stage('Run Latest Build') {
            steps {
                script {
                    // Run the container and capture output
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
