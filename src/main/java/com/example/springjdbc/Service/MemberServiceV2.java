package com.example.springjdbc.Service;

import com.example.springjdbc.domain.Member;
import com.example.springjdbc.repository.MemberRepositoryV1;
import com.example.springjdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Description("트랜젝션 처리 예재")
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 repository;

    public void accountTransfer(String fromId, String toId, int money)
        throws SQLException{
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false); // 트랜젝션 시작
            this.bizLogic(con, fromId, toId, money);
            con.commit(); //성공시 커밋
        } catch (Exception e) {
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            this.release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int
        money) throws SQLException, IllegalAccessException {
        Member fromMember = repository.findById(con, fromId);
        Member toMember = repository.findById(con, toId);

        fromMember.setMoney(fromMember.getMoney() - money);
        this.fromMemberValidate(toMember);
        repository.update(con, fromMember);

        toMember.setMoney(toMember.getMoney() + money);
        this.toMemberValidate(toMember);
        repository.update(con, toMember);
    }

    private void fromMemberValidate(Member fromMember) throws IllegalAccessException {
        if(fromMember.getMoney() < 0) {
            throw new IllegalAccessException("금액 부족");
        }
    }

    private void toMemberValidate(Member toMember) throws IllegalAccessException {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalAccessException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        } }
}
