package com.jenkins.job.firstjenkinsjob.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchPosts() {
        String url = "https://official-joke-api.appspot.com/random_joke";
        return restTemplate.getForObject(url, String.class);
    }
}
