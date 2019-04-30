
import java.io.*;
import prototype.server.*;
import common.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server extends AbstractServer 
{
 

//Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT =5555;
  
  /**
   * The interface type variable. It allows the implementation of 
   * the display method in the client.
   */
  Display serverUI;
  
  static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  static private final String DB = "eCp4XWJvNw";
  static private final String DB_URL = "jdbc:mysql://remotemysql.com/"+ DB + "?useSSL=false";
  static private final String USER = "eCp4XWJvNw";
  static private final String PASS = "eSS7xZeTpg";

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public Server(int port) {
    super(port);
  }

   /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The interface type variable.
   */
  public Server(int port, Display serverUI) throws IOException
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  if (msg.toString().startsWith("$")) {
		  String[] arrOfStr = msg.toString().split(" ");
		  int purchaseNum = getHistory(arrOfStr[1]) ;
		  
		  if(purchaseNum  == -1) {
			this.sendToAllClients("There is no customer with that name!");
		  }
		  else
		    this.sendToAllClients("# "+purchaseNum);
	  }
	  else if (msg.toString().startsWith("&")) {
		  // code to handle updatepurchase
		  String[] arrOfStr = msg.toString().split(" ");
		  
		  int purchaseNum = Integer.parseInt(arrOfStr[1]);
		  String name = arrOfStr[2];
		  reNewPurchase(name, purchaseNum);
		  this.sendToAllClients("The purchase number has been updated!");
	  }
	  
	  else if (msg.toString().startsWith("@")) {
		  // code to handle getcustomer
		  String[] arrOfStr = msg.toString().split(" ");
		  
		  String info = getCostumerInfo(arrOfStr[1]);
		  
		  this.sendToAllClients(info);
	  }
    
  }
  
  
  private String getCostumerInfo(String name) {
    Connection conn = null;
	Statement stmt = null;
	try {
		Class.forName(JDBC_DRIVER);
		 
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		
		String sql = "SELECT * FROM purchases where Name = '" + name + "'"  ;
		ResultSet rs = stmt.executeQuery(sql);
	
		if(!rs.next()) {
			stmt.close();
			conn.close();
			return "There is no customer with that name!";
		}
		
		String Name = rs.getString("Name");
	    int purchaseNum = rs.getInt("purchase");
	    
	    String info = "The customer is " + Name + " and the purchase number is: " + purchaseNum;
	    
		stmt.close();
		conn.close();
		
		return info;
	}
	catch (SQLException se) {
		se.printStackTrace();
		System.out.println("SQLException: " + se.getMessage());
	    System.out.println("SQLState: " + se.getSQLState());
	    System.out.println("VendorError: " + se.getErrorCode());
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
    return null;
  }

  private void reNewPurchase(String name, int purchaseNum) {
    Connection conn = null;
	Statement stmt = null;
	try {
		Class.forName(JDBC_DRIVER);
		 
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
		
		PreparedStatement updatePurchase = conn.prepareStatement("update purchases set Purchase = ? where Name = ?");
		updatePurchase.setInt(1,purchaseNum);
	    updatePurchase.setString(2,name);
	    updatePurchase.executeUpdate();
	
		stmt.close();
		conn.close();
	}
	catch (SQLException se) {
		se.printStackTrace();
		System.out.println("SQLException: " + se.getMessage());
	    System.out.println("SQLState: " + se.getSQLState());
	    System.out.println("VendorError: " + se.getErrorCode());
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	
  }

  private int getHistory(String name) {
	Connection conn = null;
	Statement stmt = null;
	try {
		Class.forName(JDBC_DRIVER);
		 

		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = conn.createStatement();
	
		String sql = "SELECT * FROM purchases where Name = '" + name + "'"  ;
		ResultSet rs = stmt.executeQuery(sql);
		int purchaseNum = 0;
		if(!rs.next()) {
			stmt.close();
			conn.close();
			return -1;
		}
		
	    purchaseNum = rs.getInt("purchase");
		
		rs.close();
		stmt.close();
		conn.close();
		
		return purchaseNum;
	}
	catch (SQLException se) {
		se.printStackTrace();
		System.out.println("SQLException: " + se.getMessage());
	    System.out.println("SQLState: " + se.getSQLState());
	    System.out.println("VendorError: " + se.getErrorCode());
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
	return 0;
  }

/**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI
   */
  public void handleMessageFromServerUI(String message) {
	 if (message.charAt(0) == '#') {
	      runCommand(message);
	 }
  }

  /**
   * This method executes server commands.
   *
   * @param message String from the server console.
   */
  private void runCommand(String message) {
    if (message.equalsIgnoreCase("#quit")) {
      quit();
    }
    else if (message.equalsIgnoreCase("#stop")) {
      stopListening();
    }
    else if (message.equalsIgnoreCase("#close")) {
      try {
        close();
      }
      catch(IOException e) {}
    }
    else if (message.toLowerCase().startsWith("#setport")) {
      if (getNumberOfClients() == 0 && !isListening()) {
        // If there are no connected clients and we are not 
        // listening for new ones, assume server closed.
        // A more exact way to determine this was not obvious and
        // time was limited.
        int newPort = Integer.parseInt(message.substring(9));
        setPort(newPort);
        //error checking should be added
        serverUI.display
          ("Server port changed to " + getPort());
      }
      else {
        serverUI.display
          ("The server is not closed. Port cannot be changed.");
      }
    }
    else if (message.equalsIgnoreCase("#start")) {
      if (!isListening()) {
        try {
          listen();
        }
        catch(Exception ex) {
          serverUI.display("Error - Could not listen for clients!");
        }
      }
      else {
        serverUI.display
          ("The server is already listening for clients.");
      }
    }
    else if (message.equalsIgnoreCase("#getport")) {
      serverUI.display("Currently port: " + Integer.toString(getPort()));
    }
    
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted() {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped() {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  /**
   * Run when new clients are connected. Implemented by Benjamin Bergman,
   * Oct 22, 2009.
   *
   * @param client the connection connected to the client
   */
  protected void clientConnected(ConnectionToClient client) {
    // display on server and clients that the client has connected.
    String msg = "A Client has connected";
    System.out.println(msg);
    this.sendToAllClients(msg);
  }

  /**
   * Run when clients disconnect. Implemented by Benjamin Bergman,
   * Oct 22, 2009
   *
   * @param client the connection with the client
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    // display on server and clients when a user disconnects
    String msg = "Client has disconnected";

    System.out.println(msg);
    this.sendToAllClients(msg);
  }

  /**
   * Run when a client suddenly disconnects. Implemented by Benjamin
   * Bergman, Oct 22, 2009
   *
   * @param client the client that raised the exception
   * @param Throwable the exception thrown
   */
  synchronized protected void clientException( ConnectionToClient client, Throwable exception) {
    String msg = "Client has disconnected";

    System.out.println(msg);
    this.sendToAllClients(msg);
  }

  /**
   * This method terminates the server.
   */
  public void quit() {
    try {
      close();
    }
    catch(IOException e){
    }
    System.exit(0);
  }


  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) {
    int port = 0; //Port to listen on

    try {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t) {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    Server sv = new Server(port);
    
    try {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
