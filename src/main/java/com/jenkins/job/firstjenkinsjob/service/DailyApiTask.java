package com.jenkins.job.firstjenkinsjob.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import java.util.ArrayList;
import java.util.List;

public class DailyApiTask {
    public List<ApiResponse> runTask(ApiService apiService) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ApiResponse> result = new ArrayList<>();

        ApiResponse response = apiService.fetchPostsAsObject();
        result.add(response);

        if (!result.isEmpty()) {
            throw new Exception("Daily API Job result list is NOT empty → failing build");
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        ApiService apiService = new ApiService();
        new DailyApiTask().runTask(apiService);
    }
}
