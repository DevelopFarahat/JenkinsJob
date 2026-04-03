pipelineJob('Daily-API-Job-1') {
    description('Gradle build and run dailyApiCall')

    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Checkout') { steps { checkout scm } }
                        stage('Build & Test') { steps { sh './gradlew clean build test' } }
                        stage('Run Daily API Job') {
                            steps {
                                script {
                                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                                        def jarFile = "build/libs/FirstJenkinsJob-0.0.1-SNAPSHOT.jar"
                                        def response = sh(
                                            script: "java -jar \${jarFile} --job.name=dailyApiCall --spring.main.web-application-type=none",
                                            returnStdout: true
                                        ).trim()
                                        echo "Daily API Call raw output:\\n\${response}"
                                        def lastLine = response.readLines().last()
                                        env.API_RESULT = lastLine ?: "No API result"
                                        def parsed
                                        try { parsed = new groovy.json.JsonSlurper().parseText(lastLine) } catch (Exception e) { parsed = null }
                                        if (parsed instanceof List && !parsed.isEmpty()) {
                                            currentBuild.result = 'UNSTABLE'
                                            error("Daily API Job result list is NOT empty → marking UNSTABLE")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    post {
                        always {
                            script {
                                emailext(
                                    subject: "Daily API Report - \${currentBuild.result}",
                                    body: "<html><body><h2>Daily API Job Result: \${currentBuild.result}</h2><pre>\${env.API_RESULT}</pre></body></html>",
                                    mimeType: 'text/html',
                                    to: "mohamed.farahat.attia@gmail.com"
                                )
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox()
        }
    }
}
