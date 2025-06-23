package com.memory.job.testJob;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TestJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TestJobTasklet testJobTasklet;


    @Bean
    public Job testJob(JobRepository jobRepository, Step testStep1) {
        return new JobBuilder("testJob", jobRepository)
                .start(testStep1)
                .build();
    }

    @Bean
    public Step testStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, TestJobTasklet testJobTasklet) {
        return new StepBuilder("testStep", jobRepository)
                .tasklet(testJobTasklet, transactionManager)
                .build();
    }

}
