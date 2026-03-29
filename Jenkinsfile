stage('Run API Jobs') {
    script {
        // Map to store API results
        def apiResults = [:]

        // Function to run each API job
        def runJob = { jobName, markUnstable = false ->
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

                        // Extract JSON result
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
                            apiResults[jobName] = jsonBlock
                            echo "JSON captured: ${jsonBlock}"
                            if (markUnstable) unstable("${jobName} returned non-empty result")
                        } else {
                            apiResults[jobName] = "No JSON detected"
                        }
                    }
                }
            }

            echo "========== END ${jobName} =========="
        }

        // Run all jobs in parallel
        parallel(
            "dailyApiCall": { runJob("dailyApiCall", true) },
            "dailyApiCall2": { runJob("dailyApiCall2") },
            "dailyApiCall3": { runJob("dailyApiCall3") }
        )

        // Expose results to post stage
        env.API_RESULT = groovy.json.JsonOutput.toJson(apiResults)
    }
}