package com.jenkins.job.firstjenkinsjob.service;

import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    public ApiResponse fetchPostsAsObject() {
        String url = "https://official-joke-api.appspot.com/random_joke";
        return restTemplate.getForObject(url, ApiResponse.class);
    }
}
