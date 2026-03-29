stage('Run API Jobs') {
    steps {
        script {
            // Store all results in a map
            def apiResults = [:]

            def runJob = { jobName, markUnstable = false ->

                return {
                    echo "========== START ${jobName} =========="

                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {

                        timeout(time: 2, unit: 'MINUTES') {

                            retry(2) {
                                sleep 5

                                def response = sh(
                                    script: "java -jar ${env.JAR_FILE} --job.name=${jobName} --spring.main.web-application-type=none",
                                    returnStdout: true
                                ).trim()

                                echo "Response (${jobName}):\n${response}"

                                // Smart JSON extraction
                                def jsonBlock = null
                                if (response.contains("JSON_RESULT_START") && response.contains("JSON_RESULT_END")) {
                                    jsonBlock = response.split("JSON_RESULT_START")[1]
                                                       ?.split("JSON_RESULT_END")[0]
                                                       ?.trim()
                                } else {
                                    def jsonLine = response.readLines().findAll {
                                        it.trim().startsWith("{") || it.trim().startsWith("[")
                                    }?.last()
                                    if (jsonLine) jsonBlock = jsonLine.trim()
                                }

                                if (jsonBlock) {
                                    apiResults[jobName] = jsonBlock // ✅ Store result
                                    echo "JSON captured: ${jsonBlock}"

                                    if (markUnstable) {
                                        unstable("${jobName} returned non-empty result")
                                    }
                                } else {
                                    apiResults[jobName] = "No JSON detected"
                                }
                            }
                        }
                    }

                    echo "========== END ${jobName} =========="
                }
            }

            parallel(
                failFast: true,
                "dailyApiCall": runJob("dailyApiCall", true),
                "dailyApiCall2": runJob("dailyApiCall2"),
                "dailyApiCall3": runJob("dailyApiCall3")
            )

            // ✅ Expose results to environment for post section
            env.API_RESULT = groovy.json.JsonOutput.toJson(apiResults)
        }
    }
}