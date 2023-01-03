package com.example.springjdbc.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {


    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
    private static final String USERNAME= "root";
    private static final String PASSWORD= "1234";

    @Bean
    public DataSource dataSource () {
        return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

}
