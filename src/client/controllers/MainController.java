package client.controllers;

import java.io.IOException;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import client.Main;
import client.MarketClient;
import entities.CredentialsCouple;
import entities.Product;
import javafx.scene.Parent;
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
	
	public MarketClient client = new MarketClient(this);
	private static CredentialsCouple credentials;
	
	public Main mainApp;
	public LoaderController loaders;
	private StackPane uploader = null;
	public List<Product> forSaleProducts;
	public List<Product> ownedProducts;
	
	private LoginController loginController;
	private AppController appController;
	private ProductsController productsController; 
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
		
		productsController = new ProductsController();
		
		uploaderController = new UploaderController(this);
		loaders.getUploaderLoader().setController(uploaderController);
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

	
	/**This method is triggered by pressing the {@code Products} button of the application and it displays a 
	 * list of forSaleProducts based on the {@code input}. 
	 * code = 1 - products for sale
	 * code = 2 - owned products
	 * */
	public void showProducts(int code) {
		
		if(client.getProducts(code).isEmpty()) {
			ScrollPane empty = new ScrollPane();
			mainApp.setNewCenter(empty, null);
			return;
		}
		ScrollPane cur = new ScrollPane(productsController.getProductsView(client.getProducts(code)));
		mainApp.setNewCenter(cur,null);
	}

	
	
	/** This method displays the product uploader on the main app when the respective button is clicked.
	 * */
	public void showUploader() {

		if(uploader == null) {
			try {
				uploader = loaders.getUploaderLoader().load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mainApp.setNewCenter(null, uploader);
	}
	
	/** This method handles the product upload, an event triggered by the user to upload a product.
	 * */
	public void uploadProduct(Product prod) {
		client.startCommunication(5, prod.toString());
	}
	
	public void printUploadStatus(int st) {
		uploaderController.printStatusMessage(st==1);
		if(st == 1) {
			updateProducts();
		}
	}

	/**This method triggers client requests of owned products and for-sell products. It is triggered
	 * after upload/return/buy requests from client.*/
	private void updateProducts() {
		client.startCommunication(1, null);
		client.startCommunication(2, null);
	}
	
	
	
}
