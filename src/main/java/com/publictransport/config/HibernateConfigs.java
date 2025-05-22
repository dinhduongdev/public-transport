package com.publictransport.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

@Configuration
@PropertySource("classpath:database.properties")
public class HibernateConfigs {

    @Bean
    public DataSource dataSource(Environment env) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("hibernate.connection.driverClass"));
        dataSource.setUrl(env.getProperty("hibernate.connection.url"));
        dataSource.setUsername(env.getProperty("hibernate.connection.username"));
        dataSource.setPassword(env.getProperty("hibernate.connection.password"));
        return dataSource;
    }

    @Bean
    public Properties hibernateProperties(Environment env) {
        Properties properties = new Properties();
        properties.put(DIALECT, env.getProperty("hibernate.dialect"));
        properties.put(SHOW_SQL, env.getProperty("hibernate.showSql"));
        return properties;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(
            DataSource dataSource,
            @Qualifier("hibernateProperties") Properties hibernateProperties
    ) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan("com.publictransport.models");
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
