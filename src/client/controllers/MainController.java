package client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	public List<Product> forSaleProducts = new ArrayList<Product>();
	public List<Product> ownedProducts = new ArrayList<Product>();
	
	private LoginController loginController;
	private AppController appController;
	private ProductsController productsController; 
	private UploaderController uploaderController;
	
	private BorderPane mainView;
	
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
				appController.setUserName(username);
				
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
		updateProducts();
		
		if(client.getProducts(code).isEmpty()) {
			ScrollPane empty = new ScrollPane();
			mainApp.setNewCenter(empty, null);
			return;
		}
		
		ScrollPane cur = new ScrollPane(productsController.getProductsView(client.getProducts(code)));
		mainApp.setNewCenter(cur,null);
	}

	
	
	/** This method displays the uploader page and it is triggered when the respective
	 * button is clicked.
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
	
	/**This method triggers the method {@code printStatusMessage} in the upload controller.
	 * @see printStatusMessage*/
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
	
	/**This method allows the client to buy or return a product.
	 * Based on the current page, the client can buy (page 0) or return (page 1) a specific product.
	 * After the action the client sends a request to the server.*/
	public void productClicked(String id) {
		//Se 0 --> display buy button
		appController.displayMultiPurposeBtn(id);
	}
	
	
	/**This method is triggered by the BUY button click and asks the client to send the server
	 * a buy request of the product specified by the input string id.*/
	public void buyProduct(String id) {
		client.startCommunication(3, id);
		showProducts(1);
	}
	
	/**This method is triggered by the RETURN button click and asks the client to send the server
	 * a return request of the product specified by the input string id.*/
	public void returnProduct(String id) {
		client.startCommunication(4, id);
		showProducts(2);
	}
	
	
}
