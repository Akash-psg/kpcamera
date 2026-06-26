package com.iolite.kpcamera.attendance.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class ExternalDataSourceForMiddleWare {

	@Autowired
	private Environment env;
	
	public JdbcTemplate getMiddeleWareDataSource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName(env.getProperty("spring.datasource.middleware.driver-class-name"));
		datasource.setUrl(env.getProperty("spring.datasource.middleware.url"));
		datasource.setUsername(env.getProperty("spring.datasource.middleware.username"));
		datasource.setPassword(env.getProperty("spring.datasource.middleware.password"));
		JdbcTemplate middleWareJDBCNew = new JdbcTemplate(datasource);
		return middleWareJDBCNew;
		
	}
}
