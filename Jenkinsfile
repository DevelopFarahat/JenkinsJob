pipeline {
    agent any
    triggers {
        cron('H 9 * * *') // run daily at 9 AM
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
       stage('Run Daily API Job') {
           steps {
               script {
                   def response = sh(
                       script: "java -jar target/FirstJenkinsJob-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall",
                       returnStdout: true
                   ).trim()
                   env.API_RESULT = response
               }
           }
       }
    post {
        success {
            emailext(
                subject: "Daily API Report - SUCCESS",
                body: """Pipeline succeeded.

External API response:
${env.API_RESULT}
""",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
        failure {
            emailext(
                subject: "Daily API Report - FAILURE",
                body: "Pipeline failed. Please check Jenkins logs.",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
