<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!-- "<% %>"는 자바 코드를 실행하기 위해 사용되며, "%@>"는 JSP 페이지의 구성 설정을 위해 사용 -->
    
<!-- 필요한 클래스 임포트, PrintWriter 클래스는 클라이언트로 데이터를 출력하는 데 사용 -->
<%@ page import="user.UserDAO" %>
<%@ page import="java.io.PrintWriter" %>

<!-- request 의 문자 인코딩을 UTF-8로 설정하여 request 시 전달되는 다양한 언어의 문자 처리 -->
<% request.setCharacterEncoding("UTF-8"); %>

<!-- Bean 사용 선언 : 해당 페이지 내에서 user 객체의 속성을 쉽게 설정하고 접근 가능 -->
<jsp:useBean id="user" class="user.User" scope="page" />

<!-- Bean 속성 설정 : setProperty 액션은 요청 파라미터로부터 user 객체의
	  userID와 userPassword 속성을 설정, 사용자가 입력한 ID와 비밀번호를 객체에 바인딩하여 서버 처리 -->
<jsp:setProperty name="user" property="userID" />
<jsp:setProperty name="user" property="userPassword" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JSP 게시판 웹 사이트</title>
</head>
<body>
	<%
		String userID = null;
		
		if (session.getAttribute("userID") != null) {
			//세션 입력
			userID = (String)session.getAttribute("userID");
		}
		
		//세션 확인
		if (userID != null) {
			PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("alert('이미 로그인 되어 있습니다.');");
    		script.println("location.href = 'main.jsp'");
    		script.println("</script>");
		}
	
		UserDAO userDAO = new UserDAO();
		
	    int result = userDAO.login(user.getUserID(), user.getUserPassword());
    	if (result == 1) {
    		//세션 설정
    		session.setAttribute("userID", user.getUserID());
    		
    		PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("location.href = 'main.jsp'");
    		script.println("</script>");
    	}
    	else if (result == 0) {
    		PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("alert('비밀번호가 틀립니다.');");
    		script.println("history.back();");
    		script.println("</script>");
    	}
    	else if (result == -1) {
    		PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("alert('존재하지 않는 아이디입니다.');");
    		script.println("history.back();");
    		script.println("</script>");
    	}
    	else if (result == -2) {
    		PrintWriter script = response.getWriter();
    		script.println("<script>");
    		script.println("alert('데이터베이스 오류가 발생했습니다.');");
    		script.println("history.back();");
    		script.println("</script>");
    	}
	%>
</body>
</html>