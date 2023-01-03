package com.example.springjdbc.Service;

import com.example.springjdbc.domain.Member;
import com.example.springjdbc.repository.MemberRepositoryV3;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Description("트랜젝션 템플릿을 활용한 추상화 - try catch 문 삭제")
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 repository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 repository){
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.repository = repository;
    }

    public void accountTransfer(String fromId, String toId, int money) {
        // 성공시 commit, 실패시 rollback
        txTemplate.executeWithoutResult((status) -> {
            try {
                this.bizLogic(fromId, toId, money);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
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
