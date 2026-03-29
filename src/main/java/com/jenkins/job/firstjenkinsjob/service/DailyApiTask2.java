package com.jenkins.job.firstjenkinsjob.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;



@Component
@ConditionalOnProperty(name = "job.name", havingValue = "dailyApiCall2")
public class DailyApiTask2 implements CommandLineRunner {

    private final ApiService apiService;
    private final ObjectMapper mapper = new ObjectMapper();

    public DailyApiTask2(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Fetch API response as an object
        ApiResponse response = apiService.fetchPostsAsObject();

        // Serialize to JSON string
        String json = mapper.writeValueAsString(response);
        System.out.print(json);

        // Exit cleanly so Jenkins knows the job finished
        System.out.flush();
    }
}
