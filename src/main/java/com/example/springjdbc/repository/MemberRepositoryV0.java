package com.example.springjdbc.repository;

import com.example.springjdbc.domain.Member;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRepositoryV0 {

    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC";
    private static final String USERNAME= "root";
    private static final String PASSWORD= "1234";

    public Connection getConnection() {
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // 커넥션 생성
            con = this.getConnection();
            // sql문을 DB에 던짐
            pstmt = con.prepareStatement(sql);
            // 파라미터 매핑
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            // 쿼리 실행
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            // 시작 역순으로 close
            this.close(con, pstmt, null);
        }
    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);

            // 데이터 추출
            rs = pstmt.executeQuery();
            Member member = new Member();
            if (rs.next()) {
                // DB 컬럼으로 접근
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new RuntimeException("일치하는 데이터가 없습니다");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            this.close(con, pstmt, rs);
        }
    }


    public void update(Member member) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, member.getMoney());
            pstmt.setString(2, member.getMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
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

    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("rs close err", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("stmt close err", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("con close err", e);
            }
        }
    }
}
