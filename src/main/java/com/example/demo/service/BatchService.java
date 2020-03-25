package com.example.demo.service;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	Job job;
	
	private static final Logger logger = LoggerFactory.getLogger(BatchService.class);
	
	public void runJob() throws Exception{
		
		WatchService watchService = FileSystems.getDefault().newWatchService();
		
		Path path = Paths.get("src","main","resources","dir");
		
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		
		WatchKey key;
		
		while((key = watchService.take())!=null) {
			
			for(WatchEvent<?> event: key.pollEvents()) {
				logger.info("Kind of event " + event.kind() + " filename "+ event.context());
				
				JobParametersBuilder builder = new JobParametersBuilder();
				builder.addString("filename", event.context().toString());
				
				jobLauncher.run(job, builder.toJobParameters());
				
			}
		}
	
	}
	
}
