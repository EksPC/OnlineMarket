package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.SignatureException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import client.controllers.MainController;
import entities.CredentialsCouple;
import entities.Product;
import entities.SingleMessage;
import javafx.scene.Scene;

public class MarketClient{
	
	private static final int PORTNO = 5001;
	private static final String ADDRESS = "localhost";
	
	static private boolean connectionState = false;
	static private boolean authenticationState = false;
	static private Socket server = null;
	static private ObjectOutputStream socketOutput = null;
	static private ObjectInputStream socketInput = null;
	static private CredentialsCouple credentials = null;
	
	static private List<Product> products = null;
	static private List<Product> ownedProducts = null;
	
	private static Object response = null;
	private static Scene marketScene = null;
	
	private static MainController controller = null;
	
	private static final Logger logger = Logger.getLogger("MarketClientLogger");
	private static FileHandler logFile = null;
	
	public Socket getServer() {
		return server;
	}
	
	public ObjectOutputStream getSocketOut() {
		return socketOutput;
	}
	
	public ObjectInputStream getSocketIn() {
		return socketInput;
	}
	
	public boolean isConnected() {
		return connectionState;
	}
	
	public boolean isAuthenticated() {
		return authenticationState;
	}
	
	public List<Product> getProducts(int type){
		if(type == 1) {
			return products;
		} else if(type == 2) {
			return ownedProducts;
		}
		else return null;
		
	}
	

	
	public MarketClient(MainController cont) {
		
		controller = cont;
		
		setLogger();
		setConnection();
		
		
	}
	
	public void printMsg(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * Copy constructor
	 * */
	public MarketClient(MarketClient other) {
		connectionState = other.isConnected();
		authenticationState = other.isAuthenticated();
		server = other.getServer();
		socketInput = other.getSocketIn();
		socketOutput = other.getSocketOut();
	}
	
	
	/**@hidden
	 * This method sets up the logger of this class
	 * 
	 * */
	private static boolean setLogger() {
		
		System.out.println("Creatig logger");
		LogManager.getLogManager().reset();
		logger.setLevel(Level.ALL);
		
		try {
			logFile = new FileHandler("clientLogger.log");
			logFile.setLevel(Level.FINE);
			logger.addHandler(logFile);
			logFile.setFormatter(new SimpleFormatter());
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Logger not initialized");
			return false;
		}
		logger.log(Level.FINEST,"logger initialised");
		return true;
	}
	
	
	/**
	 * This method gets the credentials from input {@code market.view}
	 * and send them to server by a {@code CredentialsCouple} object.
	 * */
	@SuppressWarnings("unchecked")
	public boolean checkCredentials(CredentialsCouple credentials) {
		//CREDENTIALS INPUT
		logger.log(Level.FINE,"CLIENT - checkCredentials executed");
		try {
			
			SingleMessage inputObject = (SingleMessage) response;
			socketOutput.writeObject(credentials);
			socketOutput.flush();
			
			//CREDENTIALS VERIFICATION
			inputObject = (SingleMessage) socketInput.readObject();
			System.out.println("CLIENT - socket reading - " + inputObject.toString());
			
			//Checking server authentication
			if(inputObject.getAuthentication() == true) {
				
				System.out.println("Authenticated, welcome back " + credentials.getUsr());
				authenticationState = true;
				products = (List<Product>) socketInput.readObject();
				return true;
			
			} else {
				
				logger.log(Level.FINE,"Not authenticated.");
				return false;
			}
			
			
		} catch (IOException ioe) {
			System.out.println("CLIENT - Credentials check error - " + ioe.getLocalizedMessage());
			return false;
		} catch(ClassNotFoundException cnf) {
			System.out.println("CLIENT - Credentials check error- " + cnf.getLocalizedMessage());
			return false;
		}
		
	}
	
	
	/**
	 * This method simply sends a message via socket to the server and get the response*/
	@SuppressWarnings("unchecked")
	public void startCommunication(int code, String prod) {
		/*S:
		 * 1. Products (automatic)
		 * */
		logger.log(Level.FINE,"Sending communication - code is " + code);
		SingleMessage message = new SingleMessage(true, true);
		message.setRequest(code);
		
		try {
			logger.log(Level.FINE,"request: "+message.getRequest());
			socketOutput.writeObject(message);
			socketOutput.flush();
			
			switch(code) {
			case 1: //list of products
				products = (List<Product>) socketInput.readObject();
			case 2: //owned Products
				ownedProducts = (List<Product>) socketInput.readObject();
				break;
				
			case 5: //upload request
				notifyUpload();
				sendProductToUpload(prod);
			}
			
		
		} catch (IOException e) {
			logger.log(Level.WARNING,e.getLocalizedMessage());
		}catch(ClassNotFoundException cnf) {
			logger.log(Level.WARNING,cnf.getLocalizedMessage());
			System.out.println("CLIENT - Credentials check error- " + cnf.getLocalizedMessage());
		} catch (IllegalStateException is) {
			logger.log(Level.WARNING, "List error.\n");
		}
	}
	
	
	
	private void notifyUpload() throws IOException, ClassNotFoundException{
		SingleMessage req = new SingleMessage(connectionState, authenticationState);
		SingleMessage res = new SingleMessage(connectionState, authenticationState);
		req.setRequest(5);
		
		socketOutput.writeObject(req);
		socketOutput.flush();
		
		res = (SingleMessage)socketInput.readObject();
		//Some kind of error
		if(res.getRequest() == -1) {
			logger.log(Level.WARNING,"Update request failed.");
			return;
		}
		
	}
	
	
	/**
	 * This method handles the communication with the server to upload a product.*/
	private void sendProductToUpload(String prod) throws IOException, ClassNotFoundException{
		
		socketOutput.writeObject(prod);
		socketOutput.flush();
		
		SingleMessage res = (SingleMessage) socketInput.readObject();
		
		controller.printUploadStatus(res.getRequest());
			
	}
	
	
	
	/**
	 * This method requests a connection from server and returns the status 
	 * of the connection: 1 - connection accepted, 0 - connection denied, -1 IOException,
	 * -2 - ClassNotFoundException.
	 * 
	 * @return status of connection
	 * */
	private static int setConnection() {
		//TODO implement a logger
		logger.log(Level.FINE,"CLIENT - setConnection executed");
		try {
			//connectionState METHODS
			server = new Socket(ADDRESS,PORTNO);
			socketOutput = new ObjectOutputStream(server.getOutputStream());
			socketInput = new ObjectInputStream(server.getInputStream());
			
			//Reading connection status from server
			response = socketInput.readObject();
			
			//Check state of connection
			if((response == null) || !(response instanceof SingleMessage)){
				
				logger.log(Level.FINE,"CLIENT - connection failed");
				return 0;
			}
			
			logger.log(Level.FINE,"CLIENT - connection enstablished");
			connectionState = true;
			return 1;
			
		} catch (IOException ioe) {
			System.out.println("Connection setting error - " + ioe.getLocalizedMessage());
			return -1;
		
		} catch(ClassNotFoundException cnf) {
			System.out.println("Connection setting error - " + cnf.getLocalizedMessage());
			return -2;
		} 
		
	}
	
	
	
	
	/**
	 * This method closes every object used to communicate with the server:
	 * ObjectInputStream - ObjectOutputStream - Socket*/
	public void close() {
		try {
			logger.log(Level.FINE,"CLIENT - closing connection...");
			SingleMessage msg = new SingleMessage(false, false);
			msg.setRequest(5);
			socketOutput.writeObject(msg);
			socketOutput.flush();
			socketInput.close();
			socketOutput.close();
			server.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.FINE,e.getLocalizedMessage());
		}
	}
}
