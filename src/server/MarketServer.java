package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import entities.CredentialsCouple;
import entities.SingleMessage;
import client.MarketClient;



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
	
	/**Getter method used to obtain the current user name.*/
	public String getUserName() {
		return userCredentials.getUsr();
	}
	
	
	
	
	/**
	 * This method opens the socket port and waits for a connection from client.
	 * When the connection is established, an object is sent through the socket to notify the client.
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
					controller.updateProductsLists();
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
		
		socketOutput.writeObject(controller.getProductsList(1));
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
	 * This method handles clients requests by reading and then answering them with a response message.
	 * Requests purpose are written in {@code marketClient} section. 
	 * */
	private static void handleRequests() {
		try {
			
				Object buf;
				SingleMessage req = new SingleMessage(connectionState, authenticationState);
				
				while (connectionState) {
					
					//Read from socket
					buf = socketInput.readObject();
					
					if(buf.equals(null)) {
						System.out.println("buf is null");
						continue;
					}
										
					req = (SingleMessage) buf;
					logger.log(Level.FINE, "Request - " + req.getRequest());
					switch (req.getRequest()) {
					case (0):
						close();
						break;
					case (1):
						if(controller.getProductsList(1) == null) {
							logger.log(Level.WARNING,"Products list is null.\n");
							return;
						}
						socketOutput.writeObject(controller.getProductsList(1));
						socketOutput.flush();
						break;
					case (2):
						
						if(controller.getProductsList(2) == null) {
							logger.log(Level.WARNING,"Owned products list is null.\n");
							return;
						}
						
						socketOutput.writeObject(controller.getProductsList(2));
						socketOutput.flush();
						break;
					case (3):
						//Acquisto prodotto
						System.out.println("String "+ req.str +" received.");
						SingleMessage res = new SingleMessage(connectionState, authenticationState);
						
						if(controller.buyProduct(req.str)) {
							res.setRequest(1);
						} else {
							res.setRequest(0);
						}
						
						socketOutput.writeObject(res);
						socketOutput.flush();
						break;

					case (4):
						//Restituzione prodotto
						System.out.println("String "+ req.str +" received.");
						SingleMessage response = new SingleMessage(connectionState, authenticationState);
						
						if(controller.returnProduct(req.str)) {
							response.setRequest(1);
						} else {
							response.setRequest(0);
						}
						
						socketOutput.writeObject(response);
						socketOutput.flush();
						break;
						
					case (5):
						SingleMessage resp = new SingleMessage(connectionState, authenticationState);
						resp.setRequest(1);
						
						socketOutput.writeObject(resp);
						socketOutput.flush();
						
						handleUpload();
					}
				}
		} catch (IOException e){
			System.out.println("connection interrupted - " + e.getMessage());
			close();
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("comunication message error - " + cnfe.getMessage());
			close();
		}
	}
	
	
	/**This method reads the socket to get the product to upload, checks if it's id is valid and 
	 * writes the result on the socket.*/
	private static void handleUpload() throws IOException, ClassNotFoundException{
		SingleMessage req = new SingleMessage(connectionState, authenticationState);
		String prod = new String();
		Object buf = new Object();
		//2. Receive an object from client 
		while(!(buf = socketInput.readObject()).getClass().equals(String.class)) {
			;
		}
		
		prod = (String) buf;
		
		//3. Check
		if(controller.uploadProduct(prod)) {
			req.setRequest(1);
		} else {
			req.setRequest(0);
		}
		
		socketOutput.writeObject(req);
		socketOutput.flush();
			
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
			controller.updateProductsFile();
			socketInput.close();
			socketOutput.close();
			
			
		} catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
			return;
		}
		
	}
	
	
	/**
	 * This method creates the server logger and initialises it.
	 * */
	private static boolean setLogger() {
		
		LogManager.getLogManager().reset();
		logger.setLevel(Level.ALL);
		
		try {
			logFile = new FileHandler("serverLogger.log");
			logFile.setLevel(Level.FINE);
			logger.addHandler(logFile);
			logFile.setFormatter(new SimpleFormatter());
			
		} catch (IOException e) {
			System.out.println("Logger not initialized");
			return false;
		}
		System.out.println("Logger on");
		return true;
	}
	
	
	
	
	
	public static void main(String args[]) {
		
		System.out.println("Server on, waiting for connection...");
		setLogger();
		
		logger.log(Level.FINE, "Server on, waiting for connection...");
		if(controller != null) {
			System.out.println("Controller on");
			
		} 
		
		if(controller.getProductsList(0) != null) {
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