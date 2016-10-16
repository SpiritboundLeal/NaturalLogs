package application;

import java.sql.*;

public class warehouseConnector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException{

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306?useSSL=false","root", "root");

		Statement stmt = con.createStatement();

		String str = "CREATE SCHEMA warehouse";

		stmt.executeUpdate(str);

		str = "USE warehouse";

		stmt.executeUpdate(str);

		//str = "ALTER schema blaze Default character set = latin1";

		stmt.executeUpdate(str);
		
		str = "CREATE TABLE WOODSTOCK " +
				  "(name VARCHAR(20) not NULL, " +
				  "quantity INTEGER not NULL, " +
				  "height DOUBLE not NULL, " +
				  "length DOUBLE not NULL, " +
				  "value DOUBLE not NULL, " +
				  "PRIMARY KEY (name))";

			stmt.executeUpdate(str);
			
			str = "CREATE TABLE SCRAP " +
					  "(name VARCHAR(10) not NULL, " +
					  "volume DOUBLE not NULL, " +
					  "value DOUBLE not NULL, " +
					  "PRIMARY KEY (name))";

				stmt.executeUpdate(str);
				
				stmt.close();
				con.close();
	}

}
