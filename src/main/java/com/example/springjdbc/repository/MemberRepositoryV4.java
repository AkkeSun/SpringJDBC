package com.example.springjdbc.repository;

import com.example.springjdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

@Slf4j
@Description("JdbcTemplate을 활용한 반복구문 제거")
public class MemberRepositoryV4 {

    private final JdbcTemplate template;

    public MemberRepositoryV4(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    public void save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        template.update(sql, member.getMemberId(), member.getMoney());
    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), id);
    }

    public void update(Member member) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        template.update(sql, member.getMoney(), member.getMemberId());
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        template.update(sql, memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }
}
