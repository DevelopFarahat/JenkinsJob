pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build JAR') {
            steps {
                // Make sure Maven is installed on Jenkins
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                // Build Docker image using the JAR created in target/
                sh 'docker build -t job-app .'
            }
        }
       stage('Run Latest Build') {
           steps {
               script {
                   def result = sh(
                       script: "docker run --rm --entrypoint echo job-app 'Container built successfully'",
                       returnStdout: true
                   ).trim()
                   env.JOB_RESULT = result
               }
           }
       }

    }
    post {
        success {
            emailext(
                subject: "Latest Stable Build Result - SUCCESS",
                body: "${env.JOB_RESULT}",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
        failure {
            emailext(
                subject: "Latest Stable Build Result - FAILURE",
                body: "The pipeline failed. Please check Jenkins logs for details.",
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
