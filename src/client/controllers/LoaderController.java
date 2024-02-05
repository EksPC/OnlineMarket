package client.controllers;

import javafx.fxml.FXMLLoader;

/**This class handles every loader needed and provides an interface for easy and secure 
 * access.*/
public class LoaderController {

	private FXMLLoader appLoader;
	private FXMLLoader singleProductLoader;
	private FXMLLoader uploaderLoader;
	private FXMLLoader loginLoader;
	
	private final String appPath = "/fxml/app.fxml";
	private final String productPath = "/fxml/product.fxml";
	private final String uploaderPath = "/fxml/uploader.fxml";
	private final String loginPath = "/fxml/login.fxml";
	
	public LoaderController() {
		uploaderLoader = new FXMLLoader(getClass().getResource(uploaderPath));
		appLoader = new FXMLLoader(getClass().getResource(appPath));
		singleProductLoader = new FXMLLoader(getClass().getResource(productPath));
		loginLoader = new FXMLLoader(getClass().getResource(loginPath));
	}
	
	public FXMLLoader getLoginLoader() {
		return this.loginLoader;
	}
	
	public FXMLLoader getSingleProductLoader() {
		return this.singleProductLoader;
	}
	
	public FXMLLoader getAppLoader() {
		return this.appLoader;
	}
	
	public FXMLLoader getUploaderLoader() {
		return this.uploaderLoader;
	}
	
	
}
