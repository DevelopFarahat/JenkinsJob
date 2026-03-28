pipeline {
    agent any
    triggers {
        cron('* * * * *') // every minute
    }
    stages {
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
            email(
                subject: "Latest Stable Build Result",
                body: "${env.JOB_RESULT}",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
