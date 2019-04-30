package client;


import prototype.client.*;
import java.io.*;
import common.*;

public class ClientCommand extends AbstractClient {
	
	Display clientUI; 
	String userName = null;
	String purchaseNum = null;
	String command = null;
	
	public ClientCommand(String host, int port,Display clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		openConnection();
	}

	public void handleMessageFromClientUI(String message) throws IOException {
		// TODO Auto-generated method stub
		
		command = message;
		
		if (message.charAt(0) == '#') {
			runCommand(message);
	    }
		
		else 
	    	clientUI.display
	          ("No such command!");
		
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (command.equalsIgnoreCase("#get purchase")  && !msg.toString().equalsIgnoreCase("There is no customer with that name!")) {
			 String purchaseNum = msg.toString();
			 purchaseNum = purchaseNum.substring(purchaseNum.lastIndexOf(' ') + 1);
		   this.purchaseNum = purchaseNum;
		   
		   clientUI.display(msg.toString());
	    }
		else
		   clientUI.display(msg.toString());
		//this.purchaseNum = msg.toString();
		command = null;
	}

	private void runCommand(String message) throws IOException {
	    if (message.equalsIgnoreCase("#quit")) {
	      quit();
	    }
	    else if (message.equalsIgnoreCase("#logoff")) {
	      try {
	        closeConnection();
	      }
	      catch(IOException e) {}
	      clientUI.display("You have logged off.");
	    }
	    else if (message.toLowerCase().startsWith("#setport"))
	    {
	      // requires the command, followed by a space, then the port number
	      try {
	        int newPort = Integer.parseInt(message.substring(9));
	        setPort(newPort);
	        // error checking for syntax a possible addition
	        clientUI.display
	          ("Port changed to " + getPort());
	      }
	      catch (Exception e) {
	        System.out.println("Unexpected error while setting client port!");
	      }
	    }
	    else if (message.toLowerCase().startsWith("#sethost")) {
	      setHost(message.substring(9));
	      clientUI.display
	        ("Host changed to " + getHost());
	    }
	    else if (message.equalsIgnoreCase("#gethost")) {
	      clientUI.display("Current host: " + getHost());
	    }
	    else if (message.equalsIgnoreCase("#getport")) {
	      clientUI.display("Current port: " + Integer.toString(getPort()));
	    }
	    else if (message.equalsIgnoreCase("#set username")) {
	    	 System.out.println("Enter the user name of the Customer");
		      BufferedReader fromConsole = 
		    	        new BufferedReader(new InputStreamReader(System.in));
		      String name;
		      name = fromConsole.readLine();
		      this.userName = name;
		      purchaseNum = null;
		  	  command = null;
		  	
		    }
	    else if (message.equalsIgnoreCase("#get purchase") || message.equalsIgnoreCase("#get customer")) {
	     
	      try {
	        sendToServer(message + " " + userName);
	      }
	      catch(IOException e) {
	        clientUI.display
	          ("Could not send message to server.  Terminating client.");
	        quit();
	      }
		}
	    
	    else if (message.equalsIgnoreCase("#add purchase")) {
	    	
	    	if ( this.purchaseNum == null) {System.out.println("you have to get purchase first");}
	    	else {
	    		int result = Integer.parseInt(this.purchaseNum);
	    	this.purchaseNum = Integer.toString(++result);
	    	System.out.println
	        ("New purchase Value is " + this.purchaseNum + " type \"#update purchase\" if you want to update it" );
	    	}
	    }
	    else if (message.equalsIgnoreCase("#update purchase")) {
	    	
	    	if ( purchaseNum == null) {System.out.println("you have to get purchase first");}
	    	else {
	    	try {
	    		sendToServer(message + " " + this.purchaseNum + " " + this.userName);
	 	    }
	 	    catch(IOException e) {
	 	        clientUI.display
	 	          ("Could not send message to server.  Terminating client.");
	 	        quit();
	 	    }
	    	}
	    }
	   
	    else 
	    	clientUI.display
	          ("No such command!");
	}


	public void quit() {
	    try {
	      closeConnection();
	    }
	    catch(IOException e) {}
	    System.exit(0);
    }
	
    protected void connectionException(Exception exception){
        clientUI.display
         ("The connection to the Server (" + getHost() + ", " + getPort() + 
         ") has been disconnected");
    }
	
	
	
	
}
