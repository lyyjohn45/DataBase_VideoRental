/* Yiyuan LI
 * CSE 414 HW7
 */

import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.FileInputStream;

/**
 * Runs queries against a back-end database
 */
public class Query {
	private String configFilename;
	private Properties configProps = new Properties();

	private String jSQLDriver;
	private String jSQLUrl;
	private String jSQLUser;
	private String jSQLPassword;
	
	private String jSQLUrlCustomer;

	// DB Connection
	private Connection conn;
    private Connection customerConn;

	// Canned queries

	// LIKE does a case-insensitive match

	//search movie sql to fix the security error
	private static final String MOVIE_SEARCH_SQL =
			"SELECT * FROM movie WHERE name LIKE ? ORDER BY id";
	private PreparedStatement searchMovieStatement;
	
	private static final String FS_MOVIE_JOIN_DIRECTOR_SQL = 
			"SELECT m.id as movie_id, d.* " +
			"FROM MOVIE_DIRECTORS md, DIRECTORS d, MOVIE m "+
			"WHERE m.id = md.mid AND d.id = md.did AND m.name LIKE ? "+
			"ORDER BY movie_id;";
	
	private PreparedStatement fsMovieDirectorStatment;
	
	private static final String FS_MOVIE_JOIN_ACTOR_SQL = 
			"SELECT m.id as movie_id, a.* " +
			"FROM ACTOR a, CASTS c, MOVIE m " +
			"WHERE a.id = c.pid AND m.id = c.mid AND m.name LIKE ? "+
			"ORDER BY movie_id";
	
	private PreparedStatement fsMovieActorStatement;
	
	private static final String DIRECTOR_MID_SQL = "SELECT y.* "
					 + "FROM movie_directors x, directors y "
					 + "WHERE x.mid = ? and x.did = y.id";
	private PreparedStatement directorMidStatement;
	
	private static final String ACTOR_PID_SQL = "SELECT a.* "
			 + "FROM  ACTOR a, CASTS c, MOVIE m "
			 + "WHERE m.id = ? AND a.id = c.pid AND m.id = c.mid";
	private PreparedStatement actorPidStatement;
	
	private static final String CUSTOMER_LOGIN_SQL = 
		"SELECT * FROM customer WHERE login = ? and password = ?";
	private PreparedStatement customerLoginStatement;
	
	private static final String CUSTOMER_NAME_SQL = 
			"SELECT * FROM customer WHERE id = ?";
	private PreparedStatement customerNameStatement;
	
	private static final String RENTAL_AVAIABLE_SQL = 
			"SELECT * FROM Rental r WHERE mid = ? and status = 0;";
	private PreparedStatement rentalAvaialbeStatement;
	
	private static final String USER_INFO_SQL = 
			"SELECT c.fname, c.lname, rp.maxNumber, rp.name " +
			"FROM Customer c, RentalPlan rp " +
			"WHERE c.pid = rp.pid AND c.id = ?;";
	private PreparedStatement userInfoStatement;
	
	private static final String RENTAL_PLAN_INFO_SQL =
			"SELECT * FROM RentalPLan WHERE pid = ? ";
	private PreparedStatement rentalPlanInfoStatement;
	
	private static final String CURRENT_RENTALS_SQL = 
			"SELECT COUNT(*) "+
			"FROM Rental r " +
			"WHERE cid = ? AND status = 0;";
	private PreparedStatement currentRentalStatement;

	private static final String LIST_PLANS_SQL = 
			"SELECT * FROM RentalPlan";
	private PreparedStatement listPlansStatement;
	
	private static final String UPDATE_PLAN_SQL =
			"UPDATE Customer " +
			"SET pid = ? "+
			"WHERE id = ?;";
	private PreparedStatement updatePlanStatement;
	
	private static final String IS_VALID_PLAN_SQL =
			"SELECT COUNT(*) AS num FROM RentalPlan WHERE pid = ?";
	private PreparedStatement isValidPlanSQLStatement;
	
	private static final String IS_VALID_MOVIE_SQL =
			"SELECT COUNT(*) AS num FROM movie WHERE id = ?";
	private PreparedStatement isValidMovieSQLStatement;
	
	private static final String FIND_RENTER_ID_SQL =
			"SELECT cid FROM Rental WHERE mid = ? AND status = 0";
	private PreparedStatement findRenterIdSQLStatement;
	
	private static final String RENT_MOVIE_SQL =
			"INSERT INTO Rental VALUES(?, ?, 0, GETDATE());";
	private PreparedStatement rentMovieStatement;
	
	private static final String RETURN_MOVIE_SQL =
			"UPDATE Rental SET status = 1 WHERE cid = ? AND mid = ?";
	private PreparedStatement returnMovieStatement; 
	
	/*transaction statements*/
	private static final String BEGIN_TRANSACTION_SQL = 
		"SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
	private PreparedStatement beginTransactionStatement;

	private static final String COMMIT_SQL = "COMMIT TRANSACTION";
	private PreparedStatement commitTransactionStatement;

	private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
	private PreparedStatement rollbackTransactionStatement;
	

	public Query(String configFilename) {
		this.configFilename = configFilename;
	}

    /**********************************************************/
    /* Connection code to SQL Azure. Example code below will connect to the imdb database on Azure
       IMPORTANT NOTE:  You will need to create (and connect to) your new customer database before 
       uncommenting and running the query statements in this file .
     */

	public void openConnection() throws Exception {
		configProps.load(new FileInputStream(configFilename));

		jSQLDriver   = configProps.getProperty("videostore.jdbc_driver");
		jSQLUrl	   = configProps.getProperty("videostore.imdb_url");
		jSQLUser	   = configProps.getProperty("videostore.sqlazure_username");
		jSQLPassword = configProps.getProperty("videostore.sqlazure_password");

		jSQLUrlCustomer = configProps.getProperty("videostore.customer_url");

		/* load jdbc drivers */
		Class.forName(jSQLDriver).newInstance();

		/* open connections to the imdb database */

		conn = DriverManager.getConnection(jSQLUrl, // database
						   jSQLUser, // user
						   jSQLPassword); // password
                
		conn.setAutoCommit(true); //by default automatically commit after each statement 

		/* open connection to the customer database */
	   customerConn = DriverManager.getConnection(jSQLUrlCustomer, //customer database	
			   										jSQLUser,//user
			   										jSQLPassword);//password
	   customerConn.setAutoCommit(true); //by default automatically commit after each statement
	}

	public void closeConnection() throws Exception {
		conn.close();
		customerConn.close();
	}

    /**********************************************************/
    /* prepare all the SQL statements in this method.
      "preparing" a statement is almost like compiling it.  Note
       that the parameters (with ?) are still not filled in */

	public void prepareStatements() throws Exception {

		directorMidStatement = conn.prepareStatement(DIRECTOR_MID_SQL);
		actorPidStatement = conn.prepareStatement(ACTOR_PID_SQL);
		customerLoginStatement = customerConn.prepareStatement(CUSTOMER_LOGIN_SQL);
		customerNameStatement = customerConn.prepareStatement(CUSTOMER_NAME_SQL);
		rentalAvaialbeStatement = customerConn.prepareStatement(RENTAL_AVAIABLE_SQL);
		userInfoStatement = customerConn.prepareStatement(USER_INFO_SQL);
		currentRentalStatement = customerConn.prepareStatement(CURRENT_RENTALS_SQL);
		listPlansStatement = customerConn.prepareStatement(LIST_PLANS_SQL);
		updatePlanStatement = customerConn.prepareStatement(UPDATE_PLAN_SQL);
		rentalPlanInfoStatement = customerConn.prepareStatement(RENTAL_PLAN_INFO_SQL);
		isValidPlanSQLStatement = customerConn.prepareStatement(IS_VALID_PLAN_SQL);
		isValidMovieSQLStatement = conn.prepareStatement(IS_VALID_MOVIE_SQL);
		findRenterIdSQLStatement = customerConn.prepareStatement(FIND_RENTER_ID_SQL);
		rentMovieStatement = customerConn.prepareStatement(RENT_MOVIE_SQL);
		returnMovieStatement = customerConn.prepareStatement(RETURN_MOVIE_SQL);
		/* movie search statement to fix the security hole*/
		searchMovieStatement = conn.prepareStatement(MOVIE_SEARCH_SQL);
		fsMovieDirectorStatment = conn.prepareStatement(FS_MOVIE_JOIN_DIRECTOR_SQL);
		fsMovieActorStatement = conn.prepareStatement(FS_MOVIE_JOIN_ACTOR_SQL);
		/*transection stuff*/

		beginTransactionStatement = customerConn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = customerConn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = customerConn.prepareStatement(ROLLBACK_SQL);
	}


    /**********************************************************/
    /* Suggested helper functions; you can complete these, or write your own
       (but remember to delete the ones you are not using!) */

	public String getCustomerName(int cid) throws Exception {
		/* Find the first and last name of the current customer. */
		String lastName;
		String firstName;
		customerNameStatement.clearParameters();
		customerNameStatement.setInt(1,cid);
		ResultSet name_set = customerNameStatement.executeQuery();
		if (name_set.next()){
			firstName = name_set.getString(5);
			lastName = name_set.getString(6);
		}else
		{
			lastName = "Unknon User Lastmame";
			firstName = "Unknon User firstName";
		}
		
		name_set.close();
		return (firstName + " " + lastName);
	}

	public boolean isValidPlan(int planid) throws Exception {
		int count = 0;
		isValidPlanSQLStatement.clearParameters();
		isValidPlanSQLStatement.setInt(1,planid);
		ResultSet isValid_set = isValidPlanSQLStatement.executeQuery();
		if(isValid_set.next()) count = isValid_set.getInt(1);
		if(count > 0) return true;
		else return false;
	}

	public boolean isValidMovie(int mid) throws Exception {
		int count = 0;
		isValidMovieSQLStatement.clearParameters();
		isValidMovieSQLStatement.setInt(1,mid);
		ResultSet isValid_set = isValidMovieSQLStatement.executeQuery();
		if(isValid_set.next()) count = isValid_set.getInt(1);
		if(count > 0) return true;
		else return false;
	}
	
	public int getAvailableRentalCounts(int cid) throws Exception 
	{
		userInfoStatement.clearParameters();
		userInfoStatement.setInt(1,cid);
		
		currentRentalStatement.clearParameters();
		currentRentalStatement.setInt(1,cid);
		ResultSet info_set = userInfoStatement.executeQuery();
		ResultSet cr_set = currentRentalStatement.executeQuery();
		
		if(info_set.next() && cr_set.next())
		{
			int maxRentals = info_set.getInt(3);
			int currentRentals = cr_set.getInt(1);
			info_set.close();
			cr_set.close();
			return maxRentals - currentRentals;
		}
		return -1;
	}

	//return -1 if no valid user is found.
	private int getRenterID(int mid) throws Exception {
		int cid;
		findRenterIdSQLStatement.clearParameters();
		findRenterIdSQLStatement.setInt(1, mid);
		
		ResultSet find_r_set = findRenterIdSQLStatement.executeQuery();
		if(find_r_set.next())
		{
			cid = find_r_set.getInt(1);
		}
		else
		{
			cid = -1;
		}
		return cid;
	}

    /**********************************************************/
    /* login transaction: invoked only once, when the app is started  */
	public int transaction_login(String name, String password) throws Exception {
		/* authenticates the user, and returns the user id, or -1 if authentication fails */
		int cid;

		customerLoginStatement.clearParameters();
		customerLoginStatement.setString(1,name);
		customerLoginStatement.setString(2,password);
		ResultSet cid_set = customerLoginStatement.executeQuery();
		if (cid_set.next()) cid = cid_set.getInt(1);
		else cid = -1;
		cid_set.close();
		return(cid);
	}

	public void transaction_printPersonalData(int cid) throws Exception {
		String username;
		String planname;
		int additional;
		int currentRent = -1;
		userInfoStatement.clearParameters();
		userInfoStatement.setInt(1,cid);
		currentRentalStatement.clearParameters();
		currentRentalStatement.setInt(1,cid);

		ResultSet cr_set = currentRentalStatement.executeQuery();
		ResultSet info_set = userInfoStatement.executeQuery();
		
		if(cr_set.next())currentRent = cr_set.getInt(1);
		cr_set.close();
		if (info_set.next()){
			username = info_set.getString(1) + " " + info_set.getString(2);
			planname = info_set.getString(4);
			System.out.println("\nUser: " + username);
			additional = getAvailableRentalCounts(cid);
			System.out.println("PlanName: " + planname);
			System.out.println("number of movie(s) rented: " + currentRent);
			System.out.println("additional movies you can rent " + additional);
		}
	}


    /**********************************************************/
    /* main functions in this project: */

	public void transaction_search(int cid, String movie_title)
			throws Exception {
		/* searches for movies with matching titles: SELECT * FROM movie WHERE name LIKE movie_title */
		/* prints the movies, directors, actors, and the availability status:
		   AVAILABLE, or UNAVAILABLE, or YOU CURRENTLY RENT IT */

		searchMovieStatement.clearParameters();
		String likeSearchString = "%" + movie_title + "%";
		searchMovieStatement.setString(1, likeSearchString);
		ResultSet movie_set = searchMovieStatement.executeQuery();
		
		while (movie_set.next()) {
			int mid = movie_set.getInt(1);
			System.out.println("ID: " + mid + " NAME: "
					+ movie_set.getString(2) + " YEAR: "
					+ movie_set.getString(3));
			/* do a dependent join with directors */
			directorMidStatement.clearParameters();
			directorMidStatement.setInt(1, mid);
			ResultSet director_set = directorMidStatement.executeQuery();
			while (director_set.next()) {
				System.out.println("\t\tDirector: " + director_set.getString(3)
						+ " " + director_set.getString(2));
			}
			director_set.close();
			/* availability: "AVAILABLE" "YOU HAVE IT", "UNAVAILABLE" */
			rentalAvaialbeStatement.clearParameters();
			rentalAvaialbeStatement.setInt(1, mid);
			ResultSet availability_set = rentalAvaialbeStatement.executeQuery();
			if(!availability_set.next())
			{
				System.out.println("Availability: AVAILABLE");//*if search has no result in rental, meaning it is available
			}
			else
			{
				int takenCid = availability_set.getInt(1);
				if(takenCid == cid)
					System.out.println("Availability: YOU HAVE IT");
				else
					System.out.println("Availability: UNAVAILABLE");//*if search has a result in rental and it is status = 0, meaning it is available
			}
			availability_set.close();
			
			/*actor*/
			actorPidStatement.clearParameters();
			actorPidStatement.setInt(1, mid);
			ResultSet actor_set = actorPidStatement.executeQuery();
			
			while (actor_set.next()) {
				System.out.println("\t\tActor: " + actor_set.getString(2)
						+ " " + actor_set.getString(3));
			}
			actor_set.close();
		}
		movie_set.close();
		System.out.println();
	}

	public void transaction_choosePlan(int cid, int pid) throws Exception {
		//enforce consistency, user cannot down grade the plan if the current rented movie is larger than
		//the plan he choose to down grade
		beginTransaction();
		int currentRentalCount = -1;
		int chosenPlanRentalCount = -1;
		currentRentalStatement.clearParameters();
		currentRentalStatement.setInt(1, cid);
		ResultSet cr_set = currentRentalStatement.executeQuery();
		if(cr_set.next()) currentRentalCount = cr_set.getInt(1);
		
		rentalPlanInfoStatement.clearParameters();
		rentalPlanInfoStatement.setInt(1, pid);
		ResultSet rp_info_set = rentalPlanInfoStatement.executeQuery();
		if(rp_info_set.next()) chosenPlanRentalCount = rp_info_set.getInt(3);
		
		if(!isValidPlan(pid))
		{
			System.out.println("Invalid Plan Id!");
			rollbackTransaction();
			return;
		}
			
		if(chosenPlanRentalCount < currentRentalCount)
		{
			System.out.println("You current count of rented movie exceeds the plan you chose. Please retun rented movies and try again. ");
			System.out.println("Plan update failed!");
			rollbackTransaction();
			return;
		}
		
		System.out.println("Switching your plan...");
		updatePlanStatement.clearParameters();
		updatePlanStatement.setInt(1, pid);
		updatePlanStatement.setInt(2, cid);
		updatePlanStatement.executeUpdate();
		commitTransaction();
		System.out.println("Plan updated!");
		
		cr_set.close();
		rp_info_set.close();
	}

	public void transaction_listPlans() throws Exception {
	    /* println all available plans*/
		listPlansStatement.clearParameters();
		ResultSet lp_set = listPlansStatement.executeQuery();
		String outputS;
		while(lp_set.next())
		{
			outputS = "<Plan ID> " + lp_set.getString(1) + " <Plan Name> " + lp_set.getString(2) + 
						" <Max Rental Number> " + lp_set.getString(3) + " <Monthly Fee> " + lp_set.getString(4);
			System.out.println(outputS);
		}
		lp_set.close();
	}

	public void transaction_rent(int cid, int mid) throws Exception {
	    /* rent the movie mid to the customer cid */
	    /* remember to enforce consistency ! */
		beginTransaction();
		if(!isValidMovie(mid))
		{
			System.out.println("Invalid movie ID!");
			rollbackTransaction();
			return;
		}
		
		//keep c1: at any time a movie can be rented to at most one customer.
		int renter = getRenterID(mid);
		if(renter >= 0)
		{
			System.out.println("This movie has been rented out to another userID " + renter + "!");
			rollbackTransaction();
			return;
		}
		
		//keep c2: at any time a customer can have at most as many movies rented as his/her plan allows.
		int currentRentalLeft = getAvailableRentalCounts(cid);
		if(currentRentalLeft < 1)
		{
			System.out.println("You cannot rent additional movie. return a movie and try a gain!");
			rollbackTransaction();
			return;
		}
		
		System.out.println("Renting Movie...");
		rentMovieStatement.clearParameters();
		rentMovieStatement.setInt(1, cid);
		rentMovieStatement.setInt(2, mid);
		rentMovieStatement.executeUpdate();
		commitTransaction();
		System.out.println("Rent Movie Succuessfully!");
	}

	public void transaction_return(int cid, int mid) throws Exception {
		beginTransaction();
		if(!isValidMovie(mid))
		{
			System.out.println("Invalid movie ID!");
			rollbackTransaction();
			return;
		}
		
		int renter = getRenterID(mid);
		if(renter != cid)
		{
			System.out.println("You did not rent this movie before!");
			rollbackTransaction();
			return;
		}
		
		
		System.out.println("Returning Movie...");
		returnMovieStatement.clearParameters();
		returnMovieStatement.setInt(1, cid);
		returnMovieStatement.setInt(2, mid);
		returnMovieStatement.executeUpdate();
		commitTransaction();
		System.out.println("Returning Movie Succuessfully!");
	}

	public void transaction_fastSearch(int cid, String movie_title) throws Exception {
	
		String likeSearchString = "%" + movie_title + "%";
		searchMovieStatement.clearParameters();
		searchMovieStatement.setString(1, likeSearchString);
		
		fsMovieDirectorStatment.clearParameters();
		fsMovieDirectorStatment.setString(1, likeSearchString);
		
		fsMovieActorStatement.clearParameters();
		fsMovieActorStatement.setString(1, likeSearchString);
		
		ResultSet movie_set = searchMovieStatement.executeQuery();
		ResultSet m_a_set = fsMovieActorStatement.executeQuery();
		ResultSet m_d_set = fsMovieDirectorStatment.executeQuery();

		
		m_a_set.next();//move it to the first tuple
		m_d_set.next();//move it to the first tuple
		
		while(movie_set.next())
		{
			System.out.println("ID: " + movie_set.getString(1) + " NAME: "
					+ movie_set.getString(2) + " YEAR: "
					+ movie_set.getString(3));
			
			while((!m_d_set.isAfterLast() && (m_d_set.getInt(1) == movie_set.getInt(1)))){
				String dfname = m_d_set.getString(3);
				String dlname = m_d_set.getString(4);
				System.out.println("\t\tDirector: " + dlname
					+ " " + dfname);
				m_d_set.next();
			}
			while((!m_a_set.isAfterLast()) && (m_a_set.getInt(1) == movie_set.getInt(1)))
			{
				String afname = m_a_set.getString(3);
				String alname = m_a_set.getString(4);
				System.out.println("\t\tActor: " + afname
						+ " " + alname);
				m_a_set.next();
			}
			
		}
		m_d_set.close();
		m_a_set.close();
		movie_set.close();
		
	}


    
   public void beginTransaction() throws Exception {
	    customerConn.setAutoCommit(false);
	    beginTransactionStatement.executeUpdate();	
    }

    public void commitTransaction() throws Exception {
	    commitTransactionStatement.executeUpdate();	
	    customerConn.setAutoCommit(true);
    }
    
    public void rollbackTransaction() throws Exception {
	    rollbackTransactionStatement.executeUpdate();
	    customerConn.setAutoCommit(true);
    } 

}
