package com.example.springjdbc.Service;

import com.example.springjdbc.domain.Member;
import com.example.springjdbc.repository.MemberRepositoryV2;
import com.example.springjdbc.repository.MemberRepositoryV3;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RequiredArgsConstructor
@Description("스프링이 제공하는 추상화 기술 사용 - 트랜젝션 메니저")
@Slf4j
public class MemberServiceV3_1 {

    // JDBC인 경우 DriverManagerDataSource 주입
    // JPA인 경우에는 JpaTransactionManager 주입
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 repository;

    public void accountTransfer(String fromId, String toId, int money)
        throws SQLException{

        // 트랜젝션 시작 (커밋하거나 롤백하면 자동으로 종료된다)
        TransactionStatus status =
            transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            this.bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); // 실패시 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int
        money) throws SQLException, IllegalAccessException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        fromMember.setMoney(fromMember.getMoney() - money);
        this.fromMemberValidate(toMember);
        repository.update(fromMember);

        toMember.setMoney(toMember.getMoney() + money);
        this.toMemberValidate(toMember);
        repository.update(toMember);
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
}
