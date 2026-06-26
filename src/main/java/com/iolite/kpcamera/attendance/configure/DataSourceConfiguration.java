package com.iolite.kpcamera.attendance.configure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class DataSourceConfiguration {

	@Autowired
	private Environment env;
	
	@Bean
	public DataSource mainDataSource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		try {
			datasource.setDriverClassName(env.getProperty("spring.datasource.clint.driver-class-name"));
			datasource.setUrl(env.getProperty("spring.datasource.clint.url"));
			datasource.setUsername(env.getProperty("spring.datasource.clint.username"));
			datasource.setPassword(env.getProperty("spring.datasource.clint.password"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datasource;
	}
	
	@Bean
	public DataSource mainDataSourceLive() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		try {
			datasource.setDriverClassName(env.getProperty("spring.datasource.clint.driver-class-name"));
			datasource.setUrl(env.getProperty("spring.datasource.live.url"));
			datasource.setUsername(env.getProperty("spring.datasource.clint.username"));
			datasource.setPassword(env.getProperty("spring.datasource.live.password"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datasource;
	}
	
	@Bean
	public JdbcTemplate cristMysoreJDBC(@Qualifier("mainDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean
	public JdbcTemplate cristMysoreLiveJDBC(@Qualifier("mainDataSourceLive") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean
	public DataSource secondDataSource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		try {
			datasource.setDriverClassName(env.getProperty("spring.datasource.middleware.driver-class-name"));
			datasource.setUrl(env.getProperty("spring.datasource.middleware.url"));
			datasource.setUsername(env.getProperty("spring.datasource.middleware.username"));
			datasource.setPassword(env.getProperty("spring.datasource.middleware.password"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datasource;
	}
	
	@Bean
	public JdbcTemplate middleWareJDBC(@Qualifier("secondDataSource") DataSource ds) {
		System.out.println("Initializing middleWareJDBC");
		return new JdbcTemplate(ds);
	}
	
	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}
	
}
