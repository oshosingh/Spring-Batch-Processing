package com.example.demo.batchconfig;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.demo.model.User;

@Configuration
@EnableBatchProcessing
@SuppressWarnings({"rawtypes","unchecked"})
public class BatchConfiguration {
	
	@Autowired
	DataSource datasource;

	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory  stepBuilderFactory;
	
	@Autowired
	JobCompletionNotificationListener listener;
	
	private String WILL_BE_PROVIDED;
	
	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		datasource.setUrl("jdbc:mysql://localhost/springbatch");
		datasource.setUsername("root");
		datasource.setPassword("9333");
		return datasource;
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<?> reader(@Value("#{jobParameters['filename']}") String filename){
		
		FlatFileItemReader<?> fileReader = new FlatFileItemReader<>();
		fileReader.setResource(new ClassPathResource("dir/"+ filename));
		fileReader.setLinesToSkip(1);
		fileReader.setLineMapper(new DefaultLineMapper() {{
			
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"text","duplicate"});
			}});
			
			setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
				setTargetType(User.class);
			}});
			
		}});
		
		return fileReader;
	}
	
	@Bean
	public JdbcBatchItemWriter<?> writer(){
		
		JdbcBatchItemWriter<?> fileWriter = new JdbcBatchItemWriter();
		fileWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		fileWriter.setSql("insert into user(text,duplicate) values (:text, :duplicate)");
		fileWriter.setDataSource(datasource);
		
		return fileWriter;
		
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").chunk(128)
				.reader(reader(WILL_BE_PROVIDED))
				.writer((ItemWriter<? super Object>) writer())
				.build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1())
				.end()
				.build();
	}
	
}