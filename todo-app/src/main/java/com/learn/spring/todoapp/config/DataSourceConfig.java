package com.learn.spring.todoapp.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary; // Added for explicit DataSource preference

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    // Spring Boot can often infer the driver, but explicit is good.
    // Defaulting to org.sqlite.JDBC if not specified in application.properties.
    @Value("${spring.datasource.driver-class-name:org.sqlite.JDBC}")
    private String datasourceDriverClassName;

    // SQLite typically doesn't require username/password for local file DBs.
    // If your setup requires them, uncomment and configure these properties:
    // @Value("${spring.datasource.username:}")
    // private String datasourceUsername;

    // @Value("${spring.datasource.password:}")
    // private String datasourcePassword;

    // Ensure this DataSource bean is preferred
    @Bean
    @Primary
    DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(datasourceDriverClassName);
        dataSourceBuilder.url(datasourceUrl);
        // If username and password are required, uncomment the following lines:
        // if (datasourceUsername != null && !datasourceUsername.isEmpty()) {
        //     dataSourceBuilder.username(datasourceUsername);
        // }
        // if (datasourcePassword != null && !datasourcePassword.isEmpty()) {
        //     dataSourceBuilder.password(datasourcePassword);
        // }
        return dataSourceBuilder.build();
    }
}