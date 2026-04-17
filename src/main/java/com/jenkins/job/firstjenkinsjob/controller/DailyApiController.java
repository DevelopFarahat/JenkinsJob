package com.jenkins.job.firstjenkinsjob.controller;

import com.jenkins.job.firstjenkinsjob.service.ApiService;
import com.jenkins.job.firstjenkinsjob.service.DailyApiTask;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DailyApiController {

    private final ApiService apiService;
    private final DailyApiTask dailyApiTask;

    public DailyApiController(ApiService apiService, DailyApiTask dailyApiTask) {
        this.apiService = apiService;
        this.dailyApiTask = dailyApiTask;
    }

    @GetMapping("/daily-api-task")
    public List<ApiResponse> runDailyApiTask() throws Exception {
        return dailyApiTask.runTask(apiService);
    }
}
