/*
 *
 * SPRING 2022 Validation Code (DO NOT DELETE):  SP22xyzalsiwe983HH43
 *
 * CS 470 Database Systems Lab4
 *
 * Author: Christiaan Masucci
 *
 * Date: 04/14/2022
 */

import java.awt.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

class lab4
{

	static BufferedReader keyboard;
	static Connection conn;
	static Statement stmt;

	public static void main (String args [])
			throws IOException
	{
		String username="ora_cvm100", password = "CS470_1447";
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
				System.out.println( rset.getString(1));
			}
			rset.close();

			int choice = 0;
			String Pname;
			String Vname;

			while (choice != 5)     //Loop until user enters 5
			{
				System.out.print("\n\nDatabase choices:\n" +                //list of choices for user
						"1) Find how many candidates are ascosiated with certain party\n" +
						"2) Find the candidate and voting place for a every vote of a certain voter\n" +
						"3) Add a new vote made to VotesMade table\n" +
						"4) Print Election Report for Each office\n" +
						"5) Exit program\n" +
						"Selection: ");
				choice = Integer.parseInt(keyboard.readLine());             //Read in user choice

				switch (choice)             //Switch case for all choices including invalid choice
				{
					case 1:
						System.out.print("Enter the party name you would like to know about: ");
						Pname = keyboard.readLine();
						NumOfCan(Pname);
						break;
					case 2:
						System.out.print("Enter the voter name you want to know about: ");
						Vname = keyboard.readLine();
						AllVotes(Vname);
						break;
					case 3:
						InsertVote();
						break;
					case 4:
						ElectionReport();
						break;
					case 5:
						System.out.println("Thank you");
						break;
					default:
						System.out.println("Invalid entry try again(Number 1-5)");
				}

			}

		}

		catch(SQLException e)
		{
			System.out.println("Caught SQL Exception: \n     " + e);
		}
	}

	public static void NumOfCan(String PartyName) throws SQLException   //1st query counting Party Names
	{

		String CandidatesQuery =
				"SELECT PARTYNAME "
						+ "FROM labdatas22.CANDIDATES "
						+ "WHERE PARTYNAME = '" + PartyName + "'";
		ResultSet GetPName = stmt.executeQuery(CandidatesQuery);             //run query to make sur the Party exists

		if (!GetPName.next())       //if party doesn't exist
			System.out.println("Error. No such Party Name. Try again");
		else                        //if party does exist
		{
			String PartyCountQuery = "Select count(PARTYNAME) " +       //Run the Count query for the Party
					"From Labdatas22.CANDIDATES " +
					"Where PARTYNAME = '" + PartyName + "'";

			ResultSet GetPCount = stmt.executeQuery(PartyCountQuery);
			GetPCount.next();                                           //Print the result
			System.out.println("The " + PartyName + " Party has " + GetPCount.getString(1) + " candidates associated with it.");
			GetPCount.close();
		}
	}

	public static void AllVotes(String VoterName) throws SQLException       //2nd query to find candidate and coting place for certain voter
	{
		String VoterQuery =
				"SELECT VOTERNAME "
						+ "FROM labdatas22.VOTERS "
						+ "WHERE VOTERNAME = '" + VoterName + "'";
		ResultSet GetVName = stmt.executeQuery(VoterQuery);             //run query to make sure the Voter exists

		if (!GetVName.next())       //if party doesn't exist
			System.out.println("Error. No such Voter Name. Try again");
		else                        //if party does exist
		{
			String PartyCountQuery = "Select CANDIDATENAME, VOTINGPLACE " +
					"From Labdatas22.CANDIDATES INNER JOIN (SELECT * " +
					"FROM labdatas22.VOTESMADE INNER JOIN labdatas22.VOTERS USING (VOTERID) " +
					"Where VOTERNAME = '" + VoterName + "') USING (CANDIDATEID)";

			ResultSet VoteList = stmt.executeQuery(PartyCountQuery);    //run query to find all votes

			System.out.println("Candidate Name \tVoting Place");    //print result with header
			while (VoteList.next()) {
				System.out.println( VoteList.getString(1) + VoteList.getString(2));
			}
			VoteList.close();
		}

	}

	public static void InsertVote() throws SQLException, IOException
	{
		String VotePlace;
		int VoterID, CanId;

		System.out.print("Enter the voter ID you want to add: ");
		VoterID = Integer.parseInt(keyboard.readLine());
		System.out.print("Enter the candidate ID you want to add: ");
		CanId = Integer.parseInt(keyboard.readLine());
		System.out.print("Enter the voting place you want to add: ");
		VotePlace = keyboard.readLine();


		String VoterQuery =
				"SELECT VOTERNAME "
						+ "FROM VOTERS "
						+ "WHERE VOTERID = " + VoterID;
		String CandidateQuery =
				"SELECT CANDIDATEID "
						+ "FROM CANDIDATES "
						+ "WHERE CANDIDATEID = " + CanId;
		String BothQuery =
				"SELECT VOTERID "
						+ "FROM VOTESMADE "
						+ "WHERE VOTERID = " + VoterID + " AND CANDIDATEID = " + CanId;

		ResultSet GetVName = stmt.executeQuery(VoterQuery);             //run query to make sure the Voter exists
		if (!GetVName.next())       //if voter doesn't exist
			System.out.println("Error. No such Voter Name. Try again");
		ResultSet GetCName = stmt.executeQuery(CandidateQuery);
		if(!GetCName.next())   //if Candidate doesnt exist
			System.out.println("Error. No such Candidate Name. Try again");
		ResultSet GetBoth = stmt.executeQuery(BothQuery);
		if(GetBoth.next())     //if pair already exists
			System.out.println("Error. There is already a vote match. Try again");
		else                        //passes all tests
		{
			String InsertCommand = "Insert into VOTESMADE VALUES (" +
					VoterID + ", " + CanId + ", '" + VotePlace + "')";

			stmt.executeUpdate(InsertCommand);

			System.out.println("New Vote inserted successfully");
		}

	}

	public static void ElectionReport() throws SQLException, IOException
	{
		String OfficeListQuery =
				"SELECT OFFICENAME "
						+ "FROM labdatas22.OFFICE ";

		ResultSet GetOList = stmt.executeQuery(OfficeListQuery); //get a list of all offices

		List<String> Offices = new ArrayList<>();

		while (GetOList.next())
		{
			Offices.add(GetOList.getString(1));
		}

		for(String Oname: Offices) {

			String CanTableQuery =
					"SELECT CANDIDATENAME, count(CANDIDATEID) " +
							"FROM labdatas22.VOTESMADE INNER JOIN (" +
							"labdatas22.CANDIDATES INNER JOIN labdatas22.OFFICE ON (RUNNINGFOROFFICEID =OFFICEID)) " +
							"USING (CANDIDATEID) " +
							"WHERE OFFICENAME = '" + Oname + "' " +
							"GROUP BY CANDIDATENAME, CANDIDATEID " +
							"HAVING count(CANDIDATEID) > 0" +
							"ORDER BY count(CANDIDATEID) DESC";

			//run statement to get all candidates with at least 1 vote for a specific Office
			ResultSet GetCList = stmt.executeQuery(CanTableQuery);

			System.out.println("\n\tVotes for " + Oname + "\nCandidate Name\t    Number of Votes");

			while(GetCList.next())
			{
				System.out.println(GetCList.getString(1) + GetCList.getString(2));
			}

		}

	}


}


/*
 *
 * SPRING 2022 Validation Code (DO NOT DELETE):  SP22xyzalsiwe983HH43
 *
 */
