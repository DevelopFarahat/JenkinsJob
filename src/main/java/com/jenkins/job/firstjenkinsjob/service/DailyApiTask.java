package com.jenkins.job.firstjenkinsjob.service;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "job.name", havingValue = "dailyApiCall")
public class DailyApiTask implements CommandLineRunner {

    private final ApiService apiService;

    public DailyApiTask(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting daily API call...");
        String response = apiService.fetchPosts();
        System.out.println("API Call Result:\n" + response);

        // Exit so Jenkins knows the job finished
        System.exit(0);
    }
}