package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

/**This class handles the buttons of the application.*/
public class AppController implements Initializable{
	
	private MainController controller;
	
	@FXML
	private ScrollPane centerPane;
	
	@FXML
	private Button quitBtn;
	
	@FXML
	private Button productsListBtn;
	
	@FXML
	private Button returnProductsBtn;
	
	@FXML
	private Button uploadProductsBtn;
	
	@FXML
	private Text warningText;
	
	@FXML
	private void quitButtonAction(final ActionEvent event) {
		warningText.setText("closing app...");
		controller.quit();
	}
	
	/**
	 *This method regulates the left-bar button named "Products" and 
	 *displays the list of forSaleProducts*/
	@FXML
	private void productsListButtonAction(final ActionEvent event) {
		
		warningText.setText("Charging...");
		controller.showProducts(1);
	}
	
	//list of owned product event
	@FXML
	private void returnProductsButtonAction(final ActionEvent event) {
		controller.showProducts(2);	
	}
	
	//upload a product
	@FXML
	private void uploadProductButtonAction(final ActionEvent event) {
		controller.showUploader();
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public AppController(MainController mainController) {
		this.controller = mainController;
	}
	
	public void setCentralPane(ScrollPane pane) {
		centerPane = pane;
	}
	
	
}
