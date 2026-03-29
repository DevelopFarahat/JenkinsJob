package com.jenkins.job.firstjenkinsjob.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@ConditionalOnProperty(name = "job.name", havingValue = "dailyApiCall")
public class DailyApiTask implements CommandLineRunner {

    private final ApiService apiService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<ApiResponse> result = new ArrayList<>();

    public DailyApiTask(ApiService apiService) {
        this.apiService = apiService;
    }


    @Override
    public void run(String... args) throws Exception {
        // Fetch API response as an object
        ApiResponse response = apiService.fetchPostsAsObject();


        result.add(response);
        // Serialize to JSON string
        String json = mapper.writeValueAsString(result);
        System.out.print(json);

        // Exit cleanly so Jenkins knows the job finished
        System.out.flush();
    }
}
