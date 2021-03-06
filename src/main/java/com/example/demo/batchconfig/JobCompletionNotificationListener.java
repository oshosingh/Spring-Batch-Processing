package com.example.demo.batchconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		
		logger.info("Batch Job Started");
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.info("Batch Job Finished");
	}
}
