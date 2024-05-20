package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {

	private Connection conn;
	private ResultSet rs;
	
	public BbsDAO() {
		try {
			String dbURL = "jdbc:mariadb://localhost:3307/bbs";
			String dbID = "root";
			String dbPassword = "wsu1234!";
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//작성일자 메소드
	public String getDate() {
		String SQL = "SELECT NOW();";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";	//데이터베이스 오류
	}
	
	//게시글 번호 부여 메소드
	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1;	//첫번째 게시글인 경우
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;	//데이터베이스 오류
	}
	
	//글쓰기 메소드
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "INSERT INTO bbs VALUES (?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);	//글의 유효번호
			
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;	//데이터베이스 오류
	}
	
	//게시글 리스트 메소드
	public ArrayList<Bbs> getList(int pageNumber) {
		//모든 게시글을 조회함, 유효번호가 존재하고 새롭게 작성될 게시글 번호보다 작은 모든 게시글 번호를
		//내림차순 정렬로 최대 10개까지 조회
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10;";
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			//만약 현재 글이 35개라면 getNext() = 36, 4페이지이기 때문에 결과값은 6이 나옴
			//6보다 작은 5개의 게시글이 출력
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				//DB의 행 데이터를 웹 앱에서 사용할 수 있는 형태로 변환
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				list.add(bbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	//페이징 처리 메소드
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	//하나의 게시글을 보는 메소드
	public Bbs getBbs(int bbsID) {
		String SQL = "SELECT * FROM BBS WHERE bbsID = ?;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				//DB의 행 데이터를 웹 앱에서 사용할 수 있는 형태로 변환
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				return bbs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//게시글을 수정하는 메소드
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;	//데이터베이스 오류
	}
	
	//게시글을 삭제하는 메소드
	public int delete(int bbsID) {
		String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;	//데이터베이스 오류
	}
}