/*
 *
 * SPRING 2022 Validation Code (DO NOT DELETE):  SP22xyzalsiwe983HH43
 *
 * HEADER COMMENT GOES HERE
 * 
 *      All comments removed to see if you know you need to add them...
 *  If this comment remains in your file, you will lose points.
 *
 * Author: YOUR NAME GOES HERE
 *
 * Date: DATE GOES HERE
 */

import java.sql.*;
import java.io.*;

class lab4
{

    static BufferedReader keyboard;
    static Connection conn; 
    static Statement stmt;  

    public static void main (String args [])
	throws IOException
    {
	String username="oracle username goes here", password = "oracle password goes here";
	String ename;
	int original_empno=0;
	int empno;

	keyboard = new BufferedReader(new InputStreamReader (System.in));

	try { 

	   DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            System.out.println("Registered the driver...");

	    conn = DriverManager.getConnection (
                   "jdbc:oracle:thin:@oracle2.wiu.edu:1521/orclpdb1",
		    username, password);

            System.out.println("logged into oracle as " + username);

	    conn.setAutoCommit(false);

	    stmt = conn.createStatement ();

	    ResultSet rset=stmt.executeQuery ("SELECT TName " +
					      "From Tab");


	while (rset.next()) {
   	   System.out.println( rset.getString(1) );
	   }

	rset.close();
        }


	catch(SQLException e)
	    {
		System.out.println("Caught SQL Exception: \n     " + e);
	    }
    }
}

/*
 *
 * SPRING 2022 Validation Code (DO NOT DELETE):  SP22xyzalsiwe983HH43
 *
 */
