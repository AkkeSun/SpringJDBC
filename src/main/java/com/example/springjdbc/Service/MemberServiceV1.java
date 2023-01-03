package com.example.springjdbc.Service;

import com.example.springjdbc.domain.Member;
import com.example.springjdbc.repository.MemberRepositoryV1;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;

@RequiredArgsConstructor
@Description("트랜젝션을 처리하지 않은 예제")
public class MemberServiceV1 {

    private final MemberRepositoryV1 repository;

    public void accountTransfer(String fromId, String toId, int money)
        throws SQLException, IllegalAccessException {

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
