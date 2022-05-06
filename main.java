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

class Hospital
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

			int choice = 0;                 //global variables
			int SID = 0;
			int hoursWorked = 0;
			int PID = 0;
			int VID = 0;

			while (choice != 4)     //Loop until user enters 5
			{
				System.out.print("\n\nWhich type of user are you:\n" +                //list of choices for user
						"1) Manager\n" +
						"2) Medical Staff\n" +
						"3) Security\n" +
						"4) Exit program\n" +
						"Selection: ");
				choice = Integer.parseInt(keyboard.readLine());             //Read in user choice

				switch (choice)             //Switch case for all choices including invalid choice
				{
					case 1:
						System.out.print("What would you like to do:\n" +           //list options for the manager
								"1) Update hours for a staff member\n" +
								"2) View hours worked for all staff members\n " +
								"3) Set all hours back to 0\n" +
								"Selection: ");
						choice = Integer.parseInt(keyboard.readLine());
						if(choice == 1)                                                    //manager updates hours
							UpdateHours();
						else if(choice == 2)                               //manger views all hours
							ViewHours();
						else if(choice == 3)                           //manager resets all hours to 0
							ResetHours();
						else
							System.out.println("Invalid option try again");
						break;
					case 2:
						System.out.print("What would you like to do:\n" +                   //list options for medical staff
								"1) Check the information for a patient\n" +
								"2) Update the referring doctor for a patient\n " +
								"Selection: ");
						choice = Integer.parseInt(keyboard.readLine());
						if(choice == 1)                                            //find information for a patient
						{
							ViewPatient();
						}
						else if(choice == 2)                                       //Replace the existing referring doctor with a new one
						{
							UpdateDoctor();
						}
						else
							System.out.println("Invalid option try again");
						break;
					case 3:
						System.out.print("Enter the patient name to see who attended this patient (Ex. Sally Marna): ");
						String name = (keyboard.readLine());
						FindStaffVisits(name);
						break;
					case 4:
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

	public static void UpdateHours() throws SQLException, IOException   //1st updating hours worked
	{
		int SID, HoursWorked;

		System.out.print("Enter the staff id you want to update: ");
		SID = Integer.parseInt(keyboard.readLine());
		System.out.print("\nEnter how many hours they have worked: ");
		HoursWorked = Integer.parseInt(keyboard.readLine());


		String StaffQuery =
				"SELECT SName "
						+ "FROM Staff "
						+ "WHERE SID = " + SID;

		ResultSet GetSName = stmt.executeQuery(StaffQuery);             //run query to make sure the staff member exists
		if (!GetSName.next())       //if Staff member doesn't exist
			System.out.println("Error. No such Staff Name. Try again");
		else                        //passes all tests
		{
			String UpdateCommand = "UPDATE Staff " +
					"SET HoursWorked = " + HoursWorked +
					" WHERE SID = " + SID;

			stmt.executeUpdate(UpdateCommand);

			System.out.println("Staff hours updated successfully");
		}



	}

	public static void ViewHours() throws SQLException   //2nd query listing hours for all staff
	{
		String HoursQuery =
				"SELECT SName, Hours Worked "
						+ "FROM Staff ";
		ResultSet StaffHoursList = stmt.executeQuery(HoursQuery);             //run query that takes all hours for all staff

		System.out.println("Staff Name \tHours worked");    //print result with header
		while (StaffHoursList.next()) {
			System.out.println( StaffHoursList.getString(1) + StaffHoursList.getString(2));
		}
		StaffHoursList.close();

	}

	public static void ResetHours() throws SQLException   //3rd resting hours worked to 0
	{
		String UpdateCommand = "UPDATE Staff " +
				"SET HoursWorked = 0";

		stmt.executeUpdate(UpdateCommand);             //run query that reset all hours to 0

		System.out.println("Staff hours successfully reset to 0");    //print successfully sting

	}

	public static void ViewPatient() throws SQLException, IOException   //4th finding patient information
	{
		int PID;

		System.out.print("Enter the patient id you want to view: ");
		PID = Integer.parseInt(keyboard.readLine());

		String StaffQuery =
				"SELECT PName "
						+ "FROM Staff "
						+ "WHERE PID = " + PID;

		ResultSet GetSName = stmt.executeQuery(StaffQuery);             //run query to make sure the Patient exists in the system
		if (!GetSName.next())       //if Staff member doesn't exist
			System.out.println("Error. No such Patient Name. Try again");
		else                        //passes all tests
		{
			String PatientQuery = "SELECT PName, Age, Phone#, MainPhyschian " +
					"FROM Patient " +
					" WHERE PID = " + PID;

			ResultSet PatientInfo = stmt.executeQuery(PatientQuery);

			while (PatientInfo.next())
			{
				System.out.println("Patient info for: " + PatientInfo.getString(1) +
						"\nAge: " + PatientInfo.getString(2) +
						"\nPhone#: " + PatientInfo.getString(3) +
						"\nMain Physician: " + PatientInfo.getString(4));
			}
			PatientInfo.close();
		}

	}

	public static void UpdateDoctor() throws SQLException, IOException   //5th updating doctor for visit
	{
		int VID, SID;

		System.out.print("Enter the staff id you want to update: ");
		VID = Integer.parseInt(keyboard.readLine());
		System.out.print("\nEnter the id for the new referring doctor for the visit: ");
		SID = Integer.parseInt(keyboard.readLine());

		String StaffQuery =
				"SELECT SName "
						+ "FROM Staff "
						+ "WHERE SID = " + SID;

		String VisitQuery =
				"SELECT VID "
						+ "FROM Staff "
						+ "WHERE SID = " + VID;

		ResultSet GetSName = stmt.executeQuery(StaffQuery);             //run query to make sure the doctor and visit exists
		if (!GetSName.next())       //if Staff member doesn't exist
		{
			System.out.println("Error. No such Staff Name. Try again");
			return;
		}
		ResultSet GetVName = stmt.executeQuery(VisitQuery);
		if (!GetVName.next())       //if visit doesn't exist
			System.out.println("Error. No such Visit in the system. Try again");
		else                        //passes all tests
		{
			String UpdateCommand = "UPDATE Visit " +
					"SET SIDRefer = " + SID +
					" WHERE VID = " + VID;

			stmt.executeUpdate(UpdateCommand);

			System.out.println("New doctor for visit updated successfully");
		}
	}

	public static void FindStaffVisits(String name) throws SQLException, IOException   //6th finding all staff that visit a patient
	{
		String StaffQuery = "Select Staff.name, Patient.name, CheckIn.Room#, RoomCheckIn.RCTime" +
							"FROM VisitPatient " +
							"INNER JOIN Visit USING (VID) " +
							"INNER JOIN Patient USING (PID) " +
							"INNER JOIN Staff ON Visit.SIDRefer = Staff.SID " +
							"INNER JOIN RoomCheckIn on Staff.SID = RoomCheckIn.SID " +
							"INNER JOIN CheckIn on CheckIn.ID = RoomCheckIn.ID " +
							"WHERE Patient.Name = '" + name +"'";
		ResultSet result = stmt.executeQuery(StaffQuery);
		if (!GetSName.next())       //if patient doesn't exist
		{
			System.out.println("Error. No such Patient Name. Try again");
			return;
		}
		System.out.println(name + " was seen by: ");
		while (result.next())
		{
			System.out.println(result.getString(1));
		}
		result.close();
	}

}


/*
 *
 * SPRING 2022 Validation Code (DO NOT DELETE):  SP22xyzalsiwe983HH43
 *
 */
