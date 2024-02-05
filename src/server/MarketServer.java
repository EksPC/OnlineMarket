package server;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import entities.CredentialsCouple;
import entities.Product;
import entities.SingleMessage;
import src.client.MarketClient;



/**
 * The {@code MarketSerer} gives all the services to the client {@see MarketClient}
 * Every time a server object is created:
 * 1. Accepts a connection from a client
 * 2. Asks client for authentication (username - password)
 * 3. Asks client for action to take
 * 		> List of forSaleProducts
 * 		> Product bought from client {@see Product}
 * 		> closing message
 * 
 * @author EksPC
 * 
 * */

public class MarketServer {
	

	private static boolean connectionState = false;
	private static boolean authenticationState = false;
	private static ServerSocket server = null;
	private static Socket client = null;
	private static ObjectOutputStream socketOutput = null;
	private static ObjectInputStream socketInput = null;
	private static CredentialsCouple userCredentials = null;
	
	private static final int PORTNO = 5001;
	
	private static ServerController controller = new ServerController();

	private static final Logger logger = Logger.getLogger("ServerMarketLogger");
	private static FileHandler logFile = null;

	public MarketServer() {
		
		logger.log(Level.FINE,"MarketServer initialized");
		
	}
	
	public String getUserName() {
		return userCredentials.getUsr();
	}
	
	
	
	
	/**
	 * This method opens the socket port and waits for a connection from 
	 * client. When the client connects, this method notifies that using
	 * an object;
	 * 
	 * */
	private static boolean openConnection(){
		try {
			//socket creation 
			server = new ServerSocket(PORTNO);
			
			//client connection accept
			client = server.accept();
			
			//Initialising I/O streams
			socketInput = new ObjectInputStream(client.getInputStream());
			socketOutput = new ObjectOutputStream(client.getOutputStream());
			
			//Check correct connection
			if(client == null) {
				logger.log(Level.WARNING, "unable to open connection - client is null");
				return false;
			} 
			
			//Notification of connection state
			connectionState = true;
			socketOutput.writeObject(new SingleMessage(connectionState,false));
			socketOutput.flush();
			
			
		} catch(IOException e) {
			System.out.println("openConnection - "+e.getLocalizedMessage());
			logger.log(Level.SEVERE,"Connection setup failed! - "+e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * This method gets the client's credentials from socket and
	 * checks whether they're on database (credentials.txt).
	 * It sends a response message containing the authentication status.
	 * */
	private static boolean checkLogin() {
		
		boolean state = false;
		
		try {
			while(!authenticationState) {
				//initialising the message
				SingleMessage msg = new SingleMessage(connectionState,false);
				
				
				//getting credentials from client
				userCredentials = (CredentialsCouple) socketInput.readObject();
				
				//client name tracking on server
				logger.log(Level.FINE,"Credentials: " + userCredentials.getUsr() + " " + userCredentials.getPasswd());
				
				//check value
				boolean checkValue = controller.checkCredentials(userCredentials);
				
				if(!checkValue) {
				
					state = unsuccessfulLoginAction();
				
				} else {
					logger.log(Level.FINE,userCredentials.getUsr() + " authenticated.");
					state =successfulLoginAction();
			
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE,"loginCheck error - " + e.getLocalizedMessage());
		} catch(ClassNotFoundException cnf) {
			logger.log(Level.SEVERE,"loginCheck error - " + cnf.getLocalizedMessage());
		}
		
		return state;
	}
	
	
	/**This method handles a successful login by sending to the client both an authenticating
	 * message and the list of all forSaleProducts.*/
	private static boolean successfulLoginAction() throws IOException{
		
		SingleMessage msg = new SingleMessage(connectionState,false);
		
		msg.setAuthentication(true);
		logger.log(Level.FINEST,"Authenticated - "+ userCredentials.getUsr());
		authenticationState = true;
		
		socketOutput.writeObject(msg);
		socketOutput.flush();
		
		socketOutput.writeObject(controller.getProductsList());
		socketOutput.flush();
		
		return true;
	}
	
	/**This method handles an unsuccessful login by sending a a rejecting message to a client.
	 * The server then waits for another communication.*/
	private static boolean unsuccessfulLoginAction() throws IOException{
		SingleMessage msg = new SingleMessage(connectionState,false);
		msg.setAuthentication(false);
		logger.log(Level.SEVERE,"not authenticated");
		socketOutput.writeObject(msg);
		socketOutput.flush();
		return false;
	}
	
	
	
	/**
	 * The {@code connection} method establishes a connection
	 * between client and server at the specified port number.
	 * The client-server communication works with an exchange
	 * of a {@code MarketMessage} object, that contains 
	 * */
	private static void handleRequests() {
		try {
			
				
				while (connectionState) {
					System.out.println("Handling single request");
					//Read from socket
					Object buf = socketInput.readObject();
					if(buf.equals(null)) {
						System.out.println("buf is null");
						continue;
					}
					
					logger.log(Level.FINE, "Buf read: " + buf.toString());
					SingleMessage req = (SingleMessage) buf;
					switch (req.getRequest()) {
					case (0):
						close();
						break;
					case (2):
						System.out.println("Sending " + userCredentials.getUsr() + " it's list!");
						for (Product prod : controller.getOwnedProduct(userCredentials.getUsr())) {
							System.out.println("Sending: " + prod.getOwnerName());
						}
						socketOutput.writeObject(controller.getOwnedProduct(userCredentials.getUsr()));
						socketOutput.flush();
						break;
					case (3):
						//Acquisto prodotto

					case (4):
						//Restituzione prodotto

					case (5):
						//Caricamento prodotto
					}
				}
				
				
		
			
					
		} catch (IOException e){
			System.out.println("connection error - " + e.getMessage());
			close();
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("comunication message error - " + cnfe.getMessage());
			close();
		}
	}

	
	
	/**
	 * This method closes the server by interrupting closing socket. The {@code close()}
	 * method is triggered when the client sends a message with request number = 5.
	 * @throws IOException 
	 * 
	 * @see MarketClient
	 * @see SingleMessage
	 * */
	private static void close(){
		try {
			connectionState = false;
			client.close();
			socketInput.close();
			socketOutput.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * This method creates the server logger and initialises it.
	 * */
	private static boolean setLogger() {
		
		System.out.println("Creatig logger");
		LogManager.getLogManager().reset();
		logger.setLevel(Level.ALL);
		
		try {
			logFile = new FileHandler("serverLogger.log");
			logFile.setLevel(Level.FINE);
			logger.addHandler(logFile);
			logFile.setFormatter(new SimpleFormatter());
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Logger not initialized");
			return false;
		}
		return true;
	}
	
	
	
	
	
	public static void main(String args[]) {
		
		System.out.println("Server on, waiting for connection...");
		setLogger();
		
		logger.log(Level.FINE, "Server on, waiting for connection...");
		if(controller != null) {
			System.out.println("Controller on");
			
		} 
		
		if(controller.getProductsList() != null) {
			System.out.println("Products ok");
		}
		
		
		if(!openConnection()) {
			return;
		}
		
		checkLogin();
		System.out.println("Client Connected");
		
		handleRequests();
		System.out.println("Server closing...");
		logger.log(Level.FINE,"Server closing.");
		
		return;
		
	}

	
}