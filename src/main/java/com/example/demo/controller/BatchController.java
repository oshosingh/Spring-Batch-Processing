package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BatchService;

@RestController
public class BatchController {
	
	@Autowired
	BatchService batchService;
	
	@GetMapping("/batch")
	public void startJob() throws Exception {
		batchService.runJob();
	}

}
