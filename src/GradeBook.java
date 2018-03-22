import java.util.Scanner;
import java.sql.*;


public class GradeBook {

	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter username:");
		
		String user = input.next();
		
		System.out.println("Enter password:");
		
		String password = input.next();
		
		
			String connectionString = 
					"jdbc:sqlserver://localhost:1433;"
					+ "database=TestRun;"
					+ "user=" + user + ";"
					+ "password=" + password + ";";
			
			
			Connection connection = null;
			
			try {
				connection = DriverManager.getConnection(connectionString);
				System.out.println("Connection successful!");
				
				helpMessage();
				boolean cont = true;
				
				while(cont)	//reads input and performs the appropriate operation
				{
					input.hasNextLine();
					String op = input.next();
					
					cont = performOperation(connection, op, input);
				}
				
				input.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (connection != null) try {connection.close();} catch(Exception e) {}
			}
	}
			
	
	public static void helpMessage()
	{
		System.out.println("Enter the type of action you want to perform.\n"
				+ "[add] [sID (student ID)] [first] [last] [year] will allow you to add a new student in the roster\n"
				+ "[remove] [student] [sID] will remove a student from the database\n"
				+ "[remove] [test] [tID] [sID] will remove a student's test from the database\n"
				+ "[test] [tID] [sID] [student's score] [overall] will add a test into the database\n"
				+ "[calc] will calculate all the students' grades and output them\n"
				+ "[display] [student] will display the table of students\n"
				+ "[display] [test] will display the table of tests\n"
				+ "[done] if you want to terminate program\n"
				+ "[help] to if you want this message to be repeated");
	}
	
	
	public static boolean performOperation(Connection connection, String operation, Scanner input) throws SQLException
	{
		Statement statement;
		ResultSet resultSet;
		String selectSql = ""; 
		
		if(operation.equals("done"))		//break out of loop if user just inputs "done"
			return false;
		else if (operation.equals("help"))	//outputs the help message
			helpMessage();
		else if (operation.equals("display"))
		{
			String table = input.next();
			
			if(table.equals("student"))
			{
				selectSql = "SELECT * from Student;";
				
				statement = connection.createStatement();  
                resultSet = statement.executeQuery(selectSql);
                
                while(resultSet.next())
                {
                	System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " "  
                        + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
                }
			}
			else if (table.equals("test"))
			{
				selectSql = "SELECT * from Test;";
				
				statement = connection.createStatement();  
                resultSet = statement.executeQuery(selectSql); 
                
                while(resultSet.next())
                {
                	System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " "  
                        + resultSet.getString(3) + " " + resultSet.getString(4) + " "); 
                }
			}
		}
		else if (operation.equals("add"))	//adds a student to database
		{
			String sID = input.next(), first= input.next(), last= input.next(), year= input.next(); 
			selectSql = "insert into Student values (" + sID + ",'" + first + "','" + last + "'," + year + ",NULL);";
			
			statement = connection.createStatement();  
            int updated = statement.executeUpdate(selectSql); 
            
            System.out.println(updated + " row(s) were affected.");
		}
		else if (operation.equals("remove"))	//removes a student to database
		{
			String item = input.next();
			
			if(item.equals("student"))
			{
				String sID = input.next(); 
				selectSql = "delete from Student where " + sID + "=sID;";
			}
			else if (item.equals("test"))
			{
				String tID = input.next(), sID = input.next(); 
				selectSql = "delete from Test where " + sID + "=sID and " + tID + "=tID;";
			}
			statement = connection.createStatement();  


			int updated = statement.executeUpdate(selectSql); 		                
            System.out.println(updated + " row(s) were affected.");
		}
		else if (operation.equals("test"))	//adds a test to database
		{
			String tID = input.next(), sID = input.next(), score = input.next(), overall = input.next(); 
			selectSql = "insert into Test values (" + tID + "," + sID + "," + score + "," + overall + ");";
			
			statement = connection.createStatement();  

			int updated = statement.executeUpdate(selectSql); 		                
            System.out.println(updated + " row(s) were affected.");
		}
		else if (operation.equals("calc"))	//calculates all the students' grades and outputs them
		{
			
			String subq = "(SELECT sID, sum(Score) as Score, sum(Overall) as Overall from Test group by sID) as G";
			selectSql = "SELECT S.sID, S.First, S.Last, S.Year, FORMAT (100.0*Score/Overall,'0.00') from Student S, " + subq + " where S.sID = G.sID;";
			
			
			statement = connection.createStatement();  
            resultSet = statement.executeQuery(selectSql);
            
            // Print results from select statement  
            while (resultSet.next())   
            {  
                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " "  
                    + resultSet.getString(3) + " " + resultSet.getString(4) + "  %" + resultSet.getString(5));  
            } 
		}
		
		return true;
	}
	
}
