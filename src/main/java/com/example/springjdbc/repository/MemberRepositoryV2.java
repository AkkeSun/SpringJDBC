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
import org.springframework.jdbc.support.JdbcUtils;

@Slf4j
@Description("트랜젝션 예제")
public class MemberRepositoryV2 {
    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }

    public void save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = this.getConnection(dataSource);
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            // 시작 역순으로 close
            this.close(con, pstmt, null);
        }
    }

    // 트랜젝션 처리를 위해 커넥션을 주입받아 사용한다. (세션 유지)
    public Member findById(Connection con, String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
            // Connection은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    // 트랜젝션 처리를 위해 커넥션을 주입받아 사용한다. (세션 유지)
    public void update(Connection con, Member member) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, member.getMoney());
            pstmt.setString(2, member.getMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            // Connection은 여기서 닫지 않는다.
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.getConnection(dataSource);
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
