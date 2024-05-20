package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//DAO : Database Access Object
public class UserDAO {

	private Connection conn;		//자바와 데이터베이스를 연결
	private PreparedStatement pstmt;//쿼리문 대기 및 설정
	private ResultSet rs;			//결과값 받아오기
	
	public UserDAO() {
		try {
			//!!!!!!!!!!!!!!!!포트 중요!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			String dbURL = "jdbc:mariadb://localhost:3307/bbs";
			String dbID = "root";
			String dbPassword = "wsu1234!";
			Class.forName("org.mariadb.jdbc.Driver");
			//드라이버 매니저에 미리 저장했던 연결URL과 DB계정 정보를 담아 연결 설정
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString(1).equals(userPassword)) {
					return 1;	//로그인 성공
				}
				else
					return 0;	//비밀번호 불일치
			}
			return -1;	//아이디가 없음
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -2;		//데이터베이스 오류
	}
	
	public int join(User user) {
		String SQL = "INSERT INTO USER VALUES (?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, user.getUserID());
			pstmt.setString(2, user.getUserPassword());
			pstmt.setString(3, user.getUserName());
			pstmt.setString(4, user.getUserGender());
			pstmt.setString(5, user.getUserEmail());
			
			int result = pstmt.executeUpdate(); // 데이터베이스에 쿼리 실행
	        if (result == 1) { // 일반적으로 INSERT 성공 시, 영향을 받은 행의 수는 1입니다.
	            return result; // 성공적으로 데이터가 삽입되면 1 반환
	        } else {
	            return -1; // 삽입되지 않은 경우
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return -1; // 데이터베이스 오류
		}
	}
}
