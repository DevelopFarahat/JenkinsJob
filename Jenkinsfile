pipeline {
    agent any

    environment {
        API_RESULT = ""
    }
stage('Run Daily API Job') {
    steps {
        script {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                def output = sh(
                    script: 'java -jar build/libs/jenkins_job-0.0.1-SNAPSHOT.jar --job.name=dailyApiCall --spring.main.web-application-type=none',
                    returnStdout: true
                ).trim()

                echo "Full Output:\n${output}"

                // Take the last line that looks like JSON
                def jsonLine = output.readLines().reverse().find { it.trim().startsWith('[') || it.trim().startsWith('{') }
                echo "Extracted JSON:\n${jsonLine}"

                // Force string assignment
                env.API_RESULT = (jsonLine ?: "EMPTY").toString()

                if (jsonLine && jsonLine != "[]" && jsonLine != "{}") {
                    currentBuild.result = 'UNSTABLE'
                    echo "⚠️ Build marked as UNSTABLE بسبب وجود بيانات في API"
                }
            }
        }
    }
}

post {
    always {
        script {
            def apiResultForEmail = env.API_RESULT ?: "EMPTY"

            def emailBody = """<html>
                <body>
                    <h2>Pipeline Status: ${currentBuild.currentResult}</h2>
                    <p><b>API Result (dailyApiCall):</b></p>
                    <pre>${apiResultForEmail}</pre>
                </body>
            </html>"""

            emailext(
                subject: "Pipeline Status: ${currentBuild.currentResult}",
                body: emailBody,
                mimeType: 'text/html',
                to: 'mohamed.farahat.attia@gmail.com'
            )
        }
    }
}
}

