package com.example.springjdbc.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.springjdbc.domain.Member;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
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
