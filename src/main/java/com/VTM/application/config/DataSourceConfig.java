package com.VTM.application.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    // —— Primary (users DB) ——
    @Value("${spring.datasource.url}")
    private String primaryUrl;
    @Value("${spring.datasource.username}")
    private String primaryUsername;
    @Value("${spring.datasource.password}")
    private String primaryPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String primaryDriver;

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(primaryUrl);
        ds.setUsername(primaryUsername);
        ds.setPassword(primaryPassword);
        ds.setDriverClassName(primaryDriver);
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("primaryDataSource") DataSource ds) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setPackagesToScan("com.VTM.application.userAdministartion.entityOrDomain"); // ✅ Corrected path
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }

    @Bean(name = "primaryJdbcTemplate")
    @Primary
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    // —— First through Fifth datasources (only JdbcTemplates) ——
    @Bean(name = "firstDataSource")
    public DataSource firstDataSource(
            @Value("${spring.datasource.first.url}") String url,
            @Value("${spring.datasource.first.username}") String user,
            @Value("${spring.datasource.first.password}") String pass,
            @Value("${spring.datasource.first.driver-class-name}") String drv) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName(drv);
        return ds;
    }
    @Bean(name = "firstJdbcTemplate")
    public JdbcTemplate firstJdbcTemplate(@Qualifier("firstDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "secondDataSource")
    public DataSource secondDataSource(
            @Value("${spring.datasource.second.url}") String url,
            @Value("${spring.datasource.second.username}") String user,
            @Value("${spring.datasource.second.password}") String pass,
            @Value("${spring.datasource.second.driver-class-name}") String drv) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName(drv);
        return ds;
    }
    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate(@Qualifier("secondDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "thirdDataSource")
    public DataSource thirdDataSource(
            @Value("${spring.datasource.third.url}") String url,
            @Value("${spring.datasource.third.username}") String user,
            @Value("${spring.datasource.third.password}") String pass,
            @Value("${spring.datasource.third.driver-class-name}") String drv) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setDriverClassName(drv);
        return ds;
    }
    @Bean(name = "thirdJdbcTemplate")
    public JdbcTemplate thirdJdbcTemplate(@Qualifier("thirdDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }



}
