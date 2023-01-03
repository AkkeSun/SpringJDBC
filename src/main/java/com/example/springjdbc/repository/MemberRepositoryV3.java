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
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

@Slf4j
@Description("스프링이 제공하는 동기화 기술 사용 - DataSourceUtils")
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 트랜젝션 동기화를 사용하기 위해서는 DataSourceUtils 를 사용해야한다
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 트랜젝션 동기화를 사용하기 위해서는 DataSourceUtils 를 사용해야한다
        // 동기화중인 Connection 이 있다면 해당 Connection 을 반환한다
        return DataSourceUtils.getConnection(dataSource);
    }

    public void save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            this.close(con, pstmt, null);
        }
    }

    // 트렌젝션 처리를 위해 파라미터로 Connection 을 받지 않아도 된다
    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            Member member = new Member();
            if (rs.next()) {
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new RuntimeException("일치하는 데이터가 없습니다");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            // 여기서 다 닫아줘도 트랜젝션이 유지된다
            this.close(con, pstmt, rs);
        }
    }

    // 트렌젝션 처리를 위해 파라미터로 Connection 을 받지 않아도 된다
    public void update(Member member) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, member.getMoney());
            pstmt.setString(2, member.getMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            // 여기서 다 닫아줘도 트랜젝션이 유지된다
            this.close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            this.close(con, pstmt, null);
        }
    }

}
