pipelineJob('Daily-API-Job-1') {
    description('Run daily API call via REST endpoint and fail properly with email notification')

    definition {
        cps {
            script('''
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
                    // Call the Spring Boot endpoint instead of Gradle
                    sh 'curl -s http://localhost:8080/daily-api-task > build/api-response.json'

                    // The endpoint itself writes the JUnit XML file
                    junit testResults: 'build/test-results/DailyApiTaskTest.xml',
                          allowEmptyResults: false

                    // Escalate UNSTABLE → FAILURE
                    if (currentBuild.result == 'UNSTABLE') {
                        error("JUnit report contains failures → marking build as FAILED")
                    }

                    // Capture the raw XML content for email
                    if (fileExists('build/test-results/DailyApiTaskTest.xml')) {
                        def response = readFile('build/test-results/DailyApiTaskTest.xml').trim()
                        env.API_RESULT = response
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
                subject: "Daily API Job FAILED - ${currentBuild.result}",
                body: """<html><body>
<h2>Daily API Job FAILED</h2>
<p>JUnit Report:</p>
<pre>${env.API_RESULT}</pre>
<p>See Jenkins JUnit report for full details.</p>
</body></html>""",
                mimeType: 'text/html',
                to: "mohamed.farahat.attia@gmail.com"
            )
        }
    }
}
            '''.stripIndent())
            sandbox()
        }
    }
}
