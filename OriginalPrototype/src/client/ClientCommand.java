package client;


import prototype.client.*;
import java.io.*;
import common.*;

public class ClientCommand extends AbstractClient {
	
	Display clientUI; 
	String purchaseNum;
	String Name;
	public ClientCommand(String host, int port,String purchase,Display clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		this.purchaseNum = purchase;
		openConnection();
	}

	public void handleMessageFromClientUI(String message) throws IOException {
		// TODO Auto-generated method stub
		if (message.charAt(0) == '#') {
	      runCommand(message);
	    }
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.toString().startsWith("#")) {
		   String[] arrOfStr = msg.toString().split(" ");
		   this.purchaseNum = arrOfStr[1];
		   
		   clientUI.display("The purchase number is " + this.purchaseNum);
	    }
		else
		   clientUI.display(msg.toString());
		//this.purchaseNum = msg.toString();
		
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
	    else if (message.equalsIgnoreCase("#getpurchase")) {
	      System.out.println("Enter the name of the Customer");
	      BufferedReader fromConsole = 
	    	        new BufferedReader(new InputStreamReader(System.in));
	      String name;
	      name = fromConsole.readLine();
	      this.Name = name;
	      try {
	        sendToServer("$ " + name);
	      }
	      catch(IOException e) {
	        clientUI.display
	          ("Could not send message to server.  Terminating client.");
	        quit();
	      }
		}
	    
	    else if (message.equalsIgnoreCase("#addpurchase")) {
	    	int result = Integer.parseInt(this.purchaseNum);
	    	this.purchaseNum = Integer.toString(++result);
	    	System.out.println
	        ("New purchase Value is " + this.purchaseNum + " type #updatepurchase if you want to update it" );
	    }
	    else if (message.equalsIgnoreCase("#updatepurchase")) {
	    	try {
	    		sendToServer("& " + this.purchaseNum + " " + this.Name);
	 	    }
	 	    catch(IOException e) {
	 	        clientUI.display
	 	          ("Could not send message to server.  Terminating client.");
	 	        quit();
	 	    }
	    }
	    else if (message.equalsIgnoreCase("#getcustomer")) {
	    	 try {
	    		sendToServer("@ " + this.Name);
	 	     }
	 	     catch(IOException e) {
	 	        clientUI.display
	 	          ("Could not send message to server.  Terminating client.");
	 	        quit();
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
