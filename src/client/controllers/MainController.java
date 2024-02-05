package client.controllers;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import client.Main;
import client.MarketClient;
import entities.CredentialsCouple;
import entities.Product;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * This class handles communications between Main class and the various controllers.
 * The main purpose of this class is initialising every controller class by initialising a loader and 
 * binding a specific controller for it.
 * 
 * @author eskp2r
 * @version 1.0
 * */
public class MainController {

	private boolean toUpdate = true;
	
	public MarketClient client = new MarketClient(this);
	private static CredentialsCouple credentials;
	
	public Main mainApp;
	public LoaderController loaders;
	private StackPane uploader = null;
	public List<Product> forSaleProducts;
	public List<Product> ownedProducts;
	
	private LoginController loginController;
	private AppController appController;
	public ProductsController productsController; //TODO private
	private UploaderController uploaderController;
	
	private BorderPane mainView;
	
	private static final Logger logger = Logger.getLogger("MainControllerLogger");
	private static FileHandler logFile = null;
	
	public MainController(Main main){
		this.mainApp = main;
		this.loaders = new LoaderController();
//		setLogger();
		bindElements();
	}
	
	public CredentialsCouple getCredentials() {
		return credentials;
	}
	
	/**This method binds every loader to it's respective controller.*/
	private void bindElements() {
		appController = new AppController(this);
		loaders.getAppLoader().setController(appController);
		
		loginController = new LoginController(this);
		loaders.getLoginLoader().setController(loginController);
		
		productsController = new ProductsController(this);
		
		uploaderController = new UploaderController(this);
		loaders.getUploaderLoader().setController(uploaderController);
	}
	
	private static boolean setLogger() {
		
		System.out.println("Creatig logger");
		LogManager.getLogManager().reset();
		logger.setLevel(Level.ALL);
		
		try {
			logFile = new FileHandler("mainControllerLogger.log");
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
	
	
	public void printMsg(String msg) {
		mainApp.printMessage(msg);
	}
	
	/**This method checks whether the login is valid or not by invoking the {@code MarketClient}. 
	 * The method is triggered when the "SUBMIT" button of the login view is pressed.*/								
	public void checkLogin(String username, String password) {
		
		if(!client.checkCredentials(new CredentialsCouple(username, password))) {
			loginController.setWarningText("Wrong username or password!");
		} else {
			credentials = new CredentialsCouple(username, password);
			try {
				mainView = loaders.getAppLoader().load();
				ScrollPane pane = new ScrollPane(productsController.getProductsView(client.getProducts(1)));
				mainView.setCenter(pane);
				mainApp.setNewScene(mainView);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**This method returns the login scene needed to start the application.*/
	public Parent getLoginScene() {
		
		try {
			return loaders.getLoginLoader().load();
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**This method quits the Application. It is triggered by the "QUIT" button, that causes
	 * a chain-effect that closes everything from the application view to the MarketServer object.*/
	public void quit() {
		mainApp.quit();
		client.close();
	}
	
	/**This method handles the press of the {@code Products button} in the application by changing the {@code Main} view
	 * with a ScrollPane from {@code ProductsController object}.*/
//	public void showProducts() {
//		//TODO DISPLAY OPTIONS
//		
//		mainApp.setNewCenter(pane);
//	}
	
	/**This method is triggered by pressing the {@code Products} button of the application and it displays a 
	 * list of forSaleProducts based on the {@code input}. 
	 * code = 1 - products for sale
	 * code = 2 - owned products
	 * */
	public void showProducts(int code) {
		
		if(toUpdate || ownedProducts == null) {
			getUpdatedProducts();
			toUpdate = false;
		}
		
		ScrollPane cur = new ScrollPane(productsController.getProductsView(client.getProducts(code)));
		mainApp.setNewCenter(cur,null);
	}

	/**This method triggers a client-server communication requesting both the list of owned forSaleProducts and
	 * the list of products for sale. */
	private void getUpdatedProducts() {
		client.startCommunication(2);
		ownedProducts = client.getProducts(2);
		
		client.startCommunication(1);
		forSaleProducts = client.getProducts(1);
	}
	
	
	public void showUploader() {
		if(uploader == null) {
			try {
				uploader = loaders.getUploaderLoader().load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.log(Level.WARNING,e.getLocalizedMessage());
			}
		}
		
		mainApp.setNewCenter(null, uploader);
	}

	
}
