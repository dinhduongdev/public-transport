package com.publictransport.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

import static org.hibernate.cfg.JdbcSettings.DIALECT;
import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;


@Configuration
@PropertySource("classpath:database.properties")
public class HibernateConfigs {
    private final String driverClass;
    private final String url;
    private final String username;
    private final String password;
    private final Properties hibernateProperties;

    @Autowired
    public HibernateConfigs(Environment env) {
        this.driverClass = env.getProperty("hibernate.connection.driverClass");
        this.url = env.getProperty("hibernate.connection.url");
        this.username = env.getProperty("hibernate.connection.username");
        this.password = env.getProperty("hibernate.connection.password");
        this.hibernateProperties = new Properties();
        hibernateProperties.put(DIALECT, env.getProperty("hibernate.dialect"));
        hibernateProperties.put(SHOW_SQL, env.getProperty("hibernate.showSql"));
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        var sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan("com.publictransport.models");
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setHibernateProperties(this.hibernateProperties);
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
