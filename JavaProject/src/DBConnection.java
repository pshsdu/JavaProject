
import java.sql.*;

import javax.swing.text.AbstractDocument.Content;

public class DBConnection {
	
	private static int id = 0;
	private static PreparedStatement pstmt = null;
	private static Connection connection;
	
	
	public DBConnection() {
		connection = makeConnection();
	}
	
	public static Connection makeConnection() {
		// 연결하고자 하는 데이베이스 주소를 입력
		// not using SSL the verifyServerCertificate property
		String url = "jdbc:mysql://localhost:3306/passbooks?useSSL=false";
		Connection con = null;
		
		try {
			// JDBC 드라이버를 찾음
			Class.forName("com.mysql.jdbc.Driver");
			
			System.out.println("데이터베이스 연결 중..");
			// JDBC 드라이버를 이용하여 URL에 연결을 함
			con = DriverManager.getConnection(url, "root", "evev506070");
			System.out.println("데이터베이스 연결 성공!");
			
		// JDBC 드라이버가 없을 경우
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버를 찾지 못했습니다.");
		
		// 그 외의 에러
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 실패");
		}
		
		// Connection 객체 반환
		return con;
	}
	
	public boolean insert(String tag, int cost) throws SQLException {
		
		StringBuilder insertQuery = new StringBuilder();
		int id = getMaxId();
		
		System.out.println("id : " + id);
		
		// 데이터 베이스에 행을 추가하기 위한 Insert Query 지정
		insertQuery.append("insert into passbooks.passbook (id, tag, cost)");
		insertQuery.append("values (?, ?, ?)");
		
		pstmt = connection.prepareStatement(insertQuery.toString());
		
		// Query에 값을 넣어줌
		pstmt.setInt(1, ++id);
		pstmt.setString(2, tag);
		pstmt.setInt(3, cost);
		
		// execute()를 실행해보고 성공했을 때 삽입 성공 출력
		try {
			pstmt.execute();
			pstmt.close();
			return true;
		// 에러가 발생했을 때
		} catch (Exception e) {
			System.out.println("삽입 과정 중 에러 발생");
			System.out.println(e);
			pstmt.close();
			return false;
		}
		
	}
	
	public String[][] get() throws SQLException {
		
		StringBuilder getQuery = new StringBuilder();
		String[][] contents = new String[getSize()][4];
		int idx = 0;
		
		getQuery.append("select * from passbooks.passbook");
		pstmt = connection.prepareStatement(getQuery.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			contents[idx][1] = rs.getString(2);
			contents[idx][2] = Integer.toString(rs.getInt(1));
			contents[idx][0] = Integer.toString(rs.getInt(3));
			contents[idx++][3] = "";
		}
		
		pstmt.close();
		return contents;
	}
	
	public boolean delete(int id) throws SQLException {
		StringBuilder deleteQuery = new StringBuilder();
		
		deleteQuery.append("delete from passbooks.passbook where id = ?");
		
		pstmt = connection.prepareStatement(deleteQuery.toString());
		pstmt.setInt(1, id);
		
		try {
			pstmt.execute();
			pstmt.close();
			return true;
		} catch (Exception e) {
			System.out.println("삭제 과정 중 에러 발생");
			return false;
		}
	}
	
	public boolean modify(int id, String tag, int cost) throws SQLException {
		StringBuilder modifyQuery = new StringBuilder();
		
		modifyQuery.append("update passbooks.passbook set cost = ?, tag = ? where id = ?");
		
		pstmt = connection.prepareStatement(modifyQuery.toString());
		
		// Query에 값을 넣어줌
		pstmt.setInt(3, id);
		pstmt.setString(2, tag);
		pstmt.setInt(1, cost);
		
		// execute()를 실행해보고 성공했을 때 삽입 성공 출력
		try {
			pstmt.execute();
			pstmt.close();
			return true;
		// 에러가 발생했을 때
		} catch (Exception e) {
			System.out.println("수정 과정 중 에러 발생");
			return false;
		}
	}
	
	public int getSize() throws SQLException {
		StringBuilder getSizeQuery = new StringBuilder();
		int size = -1;
		
		getSizeQuery.append("select count(*) from passbooks.passbook");
		pstmt = connection.prepareStatement(getSizeQuery.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next())
			size = rs.getInt(1);
		
		pstmt.close();
		return size;
	}
	
	public int getMaxId() throws SQLException {
		StringBuilder getSizeQuery = new StringBuilder();
		int maxId = -1;
		
		getSizeQuery.append("select max(id) from passbooks.passbook");
		pstmt = connection.prepareStatement(getSizeQuery.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next())
			maxId = rs.getInt(1);
		
		pstmt.close();
		return maxId;
	}
	
	public String[][] getSearchedResult(String str) throws SQLException {
		StringBuilder getSerachQuery = new StringBuilder();
		StringBuilder getCntQuery = new StringBuilder();
		ResultSet rs;
		int idx = 0;
		int size = 0;
		
		getCntQuery.append("select count(*) from passbooks.passbook where tag = ?");
		
		pstmt = connection.prepareStatement(getCntQuery.toString());
		pstmt.setString(1, str);
		rs = pstmt.executeQuery();
		
		while(rs.next())
			size = rs.getInt(1);
		
		String[][] result= new String[size][4];
		
		getSerachQuery.append("select * from passbooks.passbook where tag = ?");
		
		pstmt = connection.prepareStatement(getSerachQuery.toString());
		pstmt.setString(1, str);
		rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result[idx][1] = rs.getString(2);
			result[idx][2] = Integer.toString(rs.getInt(1));
			result[idx][0] = Integer.toString(rs.getInt(3));
			result[idx++][3] = "";
		}
		
		pstmt.close();
		return result;
	}
	
	public String[][] spendingAnalysis() throws SQLException {
		StringBuilder getSpendingQuery = new StringBuilder();
		StringBuilder getSpendingCntQuery = new StringBuilder();
		ResultSet rs;
		int size = 0;
		int idx = 0;
		
		getSpendingCntQuery.append("select count(*) from (select count(*) from passbooks"
				+ ".passbook where cost < 0 group by tag) as pb");
		pstmt = connection.prepareStatement(getSpendingCntQuery.toString());
		rs = pstmt.executeQuery();
		
		while(rs.next())
			size = rs.getInt(1);
		
		String[][] contents = new String[size][2];
		
		getSpendingQuery.append("select tag, sum(cost) from passbooks.passbook where cost < 0 group by tag");
		pstmt = connection.prepareStatement(getSpendingQuery.toString());;
		rs = pstmt.executeQuery();
		
		while(rs.next()) {
			System.out.println(rs.getString(1) + " , " + Integer.toString(rs.getInt(2)));
			contents[idx][0] = rs.getString(1);
			contents[idx++][1] = Integer.toString(rs.getInt(2));
		}
		
		pstmt.close();
		return contents;
	}
	
	public int getTotalAmount() throws SQLException {
		StringBuilder getTotalQuery = new StringBuilder();
		int total = -1;
		
		getTotalQuery.append("select sum(cost) from passbooks.passbook");
		pstmt = connection.prepareStatement(getTotalQuery.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next())
			total = rs.getInt(1);
		
		pstmt.close();
		return total;
	}
	
	
	public String[][] getSortedResult(boolean isTag) throws SQLException{
		StringBuilder getSortedQuery = new StringBuilder();
		String[][] result = new String[getSize()][4];
		int idx = 0;
		
		if(isTag)
			getSortedQuery.append("select * from passbooks.passbook order by tag asc");
		else
			getSortedQuery.append("select * from passbooks.passbook order by cost desc");
		
		pstmt = connection.prepareStatement(getSortedQuery.toString());
		
		ResultSet rs = pstmt.executeQuery();
		
		while(rs.next()) {
			result[idx][1] = rs.getString(2);
			result[idx][2] = Integer.toString(rs.getInt(1));
			result[idx][0] = Integer.toString(rs.getInt(3));
			result[idx++][3] = "";
		}
		
		pstmt.close();
		return result;
	}

}
