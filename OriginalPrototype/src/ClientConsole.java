
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import client.*;
import common.*;



public class ClientConsole implements Display {

  final public static int DEFAULT_PORT = 5555;
  
 
  ClientCommand client;


  public ClientConsole(String host,int port,String purchase) {
    try {
      client= new ClientCommand(host, port, purchase, this);
    } 
    catch(IOException exception) {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
 * @throws IOException 
 * @throws UnknownHostException 
   */
  @SuppressWarnings("resource")


  public void accept() {
    try {
      BufferedReader fromConsole = 
        new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true) {
        message = fromConsole.readLine();  
        client.handleMessageFromClientUI(message);
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
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The user ID.
   * @param args[1] The host to connect to.
   * @param args[2] The port to connect to.
 * @throws IOException 
 * @throws UnknownHostException 
   */
  public static void main(String[] args) throws UnknownHostException, IOException 
  {
    int port = 0;  //The port number
    String host = "";
    String purchase = "";
    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    try {
      port = Integer.parseInt(args[1]);
    } catch (ArrayIndexOutOfBoundsException e){
      port = DEFAULT_PORT;
    }
    ClientConsole run = new ClientConsole(host, port, purchase);
    run.accept();  //Wait for console data
    
  }
}



