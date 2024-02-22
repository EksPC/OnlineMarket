package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
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


/**This class represents the client in the context of a client-server communication.
 * It allows the user to (indirectly) send and receive requests and responses to the server, like
 * the authentication, lists of products, action status, etc...*/
public class MarketClient{
	
	private static final int PORTNO = 5001;
	private static final String ADDRESS = "localhost";
	
	static private boolean connectionState = false;
	static private boolean authenticationState = false;
	static private Socket server = null;
	static private ObjectOutputStream socketOutput = null;
	static private ObjectInputStream socketInput = null;
	
	static private List<Product> forSaleProducts = new ArrayList<Product>();
	static private List<Product> ownedProducts = new ArrayList<Product>();
	
	private static Object response = null;
	
	private static MainController controller = null;
	
	private static final Logger logger = Logger.getLogger("MarketClientLogger");
	private static FileHandler logFile = null;
	
	/**Getter method for the field server, the socket connected to server's port.*/
	public Socket getServer() {
		return server;
	}
	
	/**Getter method for the field {@code socketOutput}, the socket output stream.*/
	public ObjectOutputStream getSocketOut() {
		return socketOutput;
	}
	
	/**Getter method for the field {@code socketInput}, the socket input stream.*/
	public ObjectInputStream getSocketIn() {
		return socketInput;
	}
	
	/**Returns true if the client is connected to the server, false otherwise.*/
	public boolean isConnected() {
		return connectionState;
	}
	
	/**Returns true if current user is authenticate, false otherwise.*/
	public boolean isAuthenticated() {
		return authenticationState;
	}
	
	/**Getter method to obtain a specific list of products.
	 * Based on {@code code input parameter} this method returns the list of products for sale, the list 
	 * of products owned by the current user or an empty list.*/
	public List<Product> getProducts(int code){
		if(code == 1) {
			return forSaleProducts;
		} else if(code == 2) {
			return ownedProducts;
		} else {
			return new ArrayList<Product>();
		}
		
	}
	

	/**Constructor.*/
	public MarketClient(MainController cont) {
		
		controller = cont;
		
		setLogger();
		setConnection();
		
		
	}
	
	
	/**@hidden
	 * This method sets up the logger of this class.
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
			System.out.println("Logger not initialized");
			return false;
		}
		logger.log(Level.FINEST,"logger initialised");
		return true;
	}
	
	
	/**
	 * This method gets the credentials from login fields and sends 
	 * them to server by a {@code CredentialsCouple} object.
	 * The response status then is returned:
	 * true for authentication, false otherwise.
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
				forSaleProducts = (List<Product>) socketInput.readObject();
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
	 * This method simply sends a message via socket to the server and get the response.
	 * It allows 6 different requests:
	 * 0 - close application (no response needed)
	 * 1 - products for sale
	 * 2 - products owned by the current user
	 * 3 - buy a product (specified by an id)
	 * 4 - return a product (specified by an id)
	 * 5 - upload a product (string format)*/
	@SuppressWarnings("unchecked")
	public boolean startCommunication(int code, String prod) {
		/*S:
		 * 1. Products (automatic)
		 * */
		logger.log(Level.FINE,"Sending request - " + code);
		SingleMessage message = new SingleMessage(true, true);
		message.setRequest(code);
		message.str = prod;
		
		try {
			
			socketOutput.writeObject(message);
			socketOutput.flush();
			
			switch(code) {
			
			case 1: //for sale products
				
				forSaleProducts = (List<Product>) socketInput.readObject();
				if(forSaleProducts != null) {
					return true;
				}
				return false;
			
			case 2: //owned Products
				ownedProducts = (List<Product>) socketInput.readObject();
				if(ownedProducts != null) {
					return true;
				}
				return false;
				
				
			case 3: //buy
				
				SingleMessage res = (SingleMessage) socketInput.readObject();
				return (res.getRequest()==1);
			
			case 4://return
				
				SingleMessage resp = (SingleMessage) socketInput.readObject();
				return (resp.getRequest()==1);
				
			case 5: //upload request
				
				notifyUpload();
				sendProductToUpload(prod);
			}
			
		
		} catch (IOException e) {
			logger.log(Level.WARNING,e.getLocalizedMessage());
			return false;
		}catch(ClassNotFoundException cnf) {
			logger.log(Level.WARNING,cnf.getLocalizedMessage());
			System.out.println("CLIENT - Credentials check error- " + cnf.getLocalizedMessage());
			return false;
		} catch (IllegalStateException is) {
			logger.log(Level.WARNING, "List error.\n");
			return false;
		}
		return false;
	}
	
	

	
	
	/**This method sends an upload request to the server, then it reads the response.
	 * If the response state is positive, another method is triggered to send the product to upload.
	 * @see sendProductToUpload*/
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
	
	
	/**This method sends the product to upload to the server and triggers the controller method to 
	 * notify the user the operation state.*/
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
			msg.setRequest(0);
			socketOutput.writeObject(msg);
			socketOutput.flush();
			socketInput.close();
			socketOutput.close();
			server.close();
			
		} catch (IOException e) {
			
			logger.log(Level.FINE,e.getLocalizedMessage());
		}
	}
}
