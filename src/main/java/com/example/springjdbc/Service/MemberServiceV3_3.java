package com.example.springjdbc.Service;

import com.example.springjdbc.domain.Member;
import com.example.springjdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Description("AOP를 활용한 추상화 -> @Transactional")
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 repository;

    @Transactional // 스프링 AOP 가 porintcut 을 자동 생성하여 트랜젝션 관리를 해준다
    public void accountTransfer(String fromId, String toId, int money)
        throws SQLException, IllegalAccessException {
        this.bizLogic(fromId, toId, money);
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
