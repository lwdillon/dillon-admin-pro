package com.dillon.lw.module.system.job;

import com.dillon.lw.framework.quartz.core.handler.JobHandler;
import com.dillon.lw.module.system.sse.TaskMessagePublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CalJob implements JobHandler {
    @Resource
    private TaskMessagePublisher taskMessagePublisher;
    @Override
    public String execute(String param) throws Exception {
        String taskId = "TASK_A";
        taskMessagePublisher.publish(taskId, "STATUS", "hello!"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));
        return "";
    }
}
