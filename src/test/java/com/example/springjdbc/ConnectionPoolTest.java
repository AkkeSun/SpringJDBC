package com.example.springjdbc;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class ConnectionPoolTest {

    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
    private static final String USERNAME= "root";
    private static final String PASSWORD= "1234";


    @Test
    @Description("DriverManager를 통해 커넥션을 직접 얻는 방법")
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("get connection={}, class={}", con1, con1.getClass());
        log.info("get connection={}, class={}", con2, con2.getClass());
    }

    @Test
    @Description("DataSource 인터페이스를 통해 커넥션을 얻는 방법")
    void DataSource() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        this.userDataSource(dataSource); // 매번 새로운 커넥션이 생성된다
    }

    @Test
    @Description("DataSource 인터페이스를 통해 커넥션풀을 생성")
    void DataSourceConnectionPool() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); // 10개의 커넥션 풀을 생성하도록 지정
        dataSource.setPoolName("MyPool");
        this.userDataSource(dataSource); // 커넥션 꺼내서 사용하기 (현재는 두 개만 꺼내서 사용함)
        Thread.sleep(1000); // 로그를 보기위한 설정
    }

    private void userDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("get connection={}, class={}", con1, con1.getClass());
        log.info("get connection={}, class={}", con2, con2.getClass());
    }
}
