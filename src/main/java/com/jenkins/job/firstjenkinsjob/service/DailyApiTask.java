package com.jenkins.job.firstjenkinsjob.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class DailyApiTask {

    public List<ApiResponse> runTask(ApiService apiService) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ApiResponse> result = new ArrayList<>();

        ApiResponse response = apiService.fetchPostsAsObject();
        result.add(response);

        // Always write a JUnit-style report
        writeJUnitReport(result);

        return result;
    }

    private void writeJUnitReport(List<ApiResponse> result) throws Exception {
        Path outputDir = Path.of("build/test-results");
        Files.createDirectories(outputDir);

        Path xmlFile = outputDir.resolve("DailyApiTaskTest.xml");

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<testsuite name=\"DailyApiTask\" tests=\"1\" failures=\"")
                .append(!result.isEmpty() ? "0" : "1")
                .append("\">\n");

        xml.append("  <testcase classname=\"DailyApiTask\" name=\"validateEmptyResult\" time=\"0\">\n");
        if (!result.isEmpty()) {
            xml.append("    <failure message=\"Result list is NOT empty\">")
                    .append("Daily API Job result list is NOT empty → failing build")
                    .append("</failure>\n");
        }
        xml.append("  </testcase>\n");
        xml.append("</testsuite>\n");

        Files.writeString(xmlFile, xml.toString());
    }

    public static void main(String[] args) throws Exception {
        ApiService apiService = new ApiService();
        new DailyApiTask().runTask(apiService);
    }
}
