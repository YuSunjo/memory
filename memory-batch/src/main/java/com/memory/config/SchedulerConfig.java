package com.memory.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final JobLauncher jobLauncher;
    private final Job testJob;

    @Scheduled(cron = "* * * * * *")
    public void runJob1() throws Exception {
        log.info("Starting batch job");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution execution = jobLauncher.run(testJob, jobParameters);
            log.info("Job finished with status: {}", execution.getStatus());
        } catch (Exception e) {
            log.error("Job failed", e);
            throw e;
        }
    }

}
