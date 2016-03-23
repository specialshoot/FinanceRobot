package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import model.Question;

public class Mysql {

	public static final String DBDRIVER = "com.mysql.jdbc.Driver";
	// 连接地址是由各个数据库生产商单独提供的，所以需要单独记住
	public static final String DBURL = "jdbc:mysql://localhost:3306/financerobot";
	// 连接数据库的用户名
	public static final String DBUSER = "root";
	// 连接数据库的密码
	public static final String DBPASS = "xuanzhi1";

	public Connection getConn() {
		Connection conn = null; // 定义一个MYSQL链接对象
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance(); // MYSQL驱动
			conn = DriverManager.getConnection(DBURL, DBUSER, DBPASS); // 链接本地MYSQL
			System.out.print("数据库连接成功");
		} catch (Exception e) {
			System.out.print("数据库连接失败.MYSQL ERROR:" + e.getMessage());
		}
		return conn;
	}

	public void addData(String question) throws SQLException {
		Connection conn = getConn();
		String insertSql = "insert into finance(question) values(?);";
		PreparedStatement psta = conn.prepareStatement(insertSql);
		psta.setString(1, question);
		// 往数据库中增加一批数据
		psta.addBatch();
		psta.executeBatch();
		psta.close();
		conn.close();
	}

	public void ddlDel(String question) throws SQLException {
		String ddlDelsql = "delete from finance where question=" + question;
		Connection conn = getConn();
		Statement sta = conn.createStatement();
		sta.executeUpdate(ddlDelsql);
		sta.close();
		conn.close();
	}

	public ArrayList<Question> ddlSelect() throws SQLException {
		ArrayList<Question> questions = new ArrayList<Question>();
		Connection conn = getConn();
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery("select * from finance");
		while (rs.next()) {
			int id = rs.getInt("id");
			String question = rs.getString("question");
			questions.add(new Question(id, question));
		}
		return questions;
	}

	public boolean ddlHasItem(String str) throws SQLException {
		ArrayList<Question> questions = new ArrayList<Question>();
		Connection conn = getConn();
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery("select * from finance where question = " + str);
		if (rs.wasNull()) {
			return false;
		}
		return true;
	}
}
