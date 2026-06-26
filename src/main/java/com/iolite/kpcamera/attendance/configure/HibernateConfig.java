package com.iolite.kpcamera.attendance.configure;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean
    public LocalSessionFactoryBean sessionFactory(@Qualifier("mainDataSource") DataSource ds) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(ds);  // Set the data source
        sessionFactory.setPackagesToScan("com.iolite.kpcamera");  // Entity package(s)

        // Set Hibernate properties (you can customize these)
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect"); // Use appropriate dialect
        hibernateProperties.put("hibernate.show_sql", "true");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "update");  // for schema generation (update/none/create)
        hibernateProperties.put("hibernate.format_sql", "true");

        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Bean
    public SessionFactory sessionFactoryBean(LocalSessionFactoryBean localSessionFactoryBean) throws Exception {
        return localSessionFactoryBean.getObject();
    }
}
