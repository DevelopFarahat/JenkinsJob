pipelineJob('Daily-API-Job-1') {
    description('Run daily API call and fail properly with email notification')

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
                    // Run the Gradle task that generates the JUnit XML
                    sh './gradlew dailyApiCall'

                    // Wrap junit so failures escalate to FAILURE
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        junit testResults: 'build/test-results/DailyApiTaskTest.xml',
                              allowEmptyResults: false
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
