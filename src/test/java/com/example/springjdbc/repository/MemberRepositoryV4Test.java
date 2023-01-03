package com.example.springjdbc.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.springjdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberRepositoryV4Test {

    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
    private static final String USERNAME= "root";
    private static final String PASSWORD= "1234";


    @Test
    void crud() throws SQLException {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); // 10개의 커넥션 풀을 생성하도록 지정
        dataSource.setPoolName("MyPool");

        MemberRepositoryV4 repository = new MemberRepositoryV4(dataSource);

        // save()
        Member member = new Member("sun", 1500);
        repository.save(member);

        // findById()
        member = repository.findById("sun");
        assertEquals(member.getMemberId(), "sun");
        assertEquals(member.getMoney(), 1500);

        // update()
        member = new Member("sun", 3000);
        repository.update(member);
        member = repository.findById("sun");
        assertEquals(member.getMemberId(), "sun");
        assertEquals(member.getMoney(), 3000);

        // delete()
        repository.delete("sun");
        assertThatThrownBy(() -> repository.findById("sun"))
            .isInstanceOf(RuntimeException.class);
    }
}
