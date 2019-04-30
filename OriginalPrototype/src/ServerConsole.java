
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import client.*;
import common.*;


public class ServerConsole implements Display {
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that created this EchoServer.
   */
  Server server;
 
  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param port The port to connect on.
   */
  public ServerConsole(int port) {
    try {
      server = new Server(port, this);
    } 
    catch(IOException exception) {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    try {
      server.listen(); //Start listening for connections
    }
    catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() {
    try {
      BufferedReader fromConsole = 
        new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) {
        message = fromConsole.readLine();
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Server UI.
   *
   * @param args[0] The port to connect to.
 * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    int port = 0;  //The port number
    
    try {
      port = Integer.parseInt(args[0]);
    }
    catch(Throwable e) {
      port = DEFAULT_PORT;
    }
    ServerConsole server = new ServerConsole(port);
    server.accept();  //Wait for console data
  }
}
//End of ServerConsole class


