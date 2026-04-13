package com.jenkins.job.firstjenkinsjob;
import com.jenkins.job.firstjenkinsjob.model.ApiResponse;
import com.jenkins.job.firstjenkinsjob.service.ApiService;
import com.jenkins.job.firstjenkinsjob.service.DailyApiTask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class DailyApiTaskTest {
    @Test
    void testRunTaskThrowsExceptionWhenResultNotEmpty() {
        ApiService mockService = mock(ApiService.class);
        when(mockService.fetchPostsAsObject()).thenReturn(new ApiResponse());

        DailyApiTask task = new DailyApiTask();

        Exception exception = assertThrows(Exception.class, () -> {
            task.runTask(mockService);
        });

        assertTrue(exception.getMessage().contains("NOT empty"));
    }

    @Test
    void testRunTaskReturnsEmptyListWhenNoResponse() throws Exception {
        ApiService mockService = mock(ApiService.class);
        when(mockService.fetchPostsAsObject()).thenReturn(null);

        DailyApiTask task = new DailyApiTask();
        assertTrue(task.runTask(mockService).isEmpty());
    }
}
