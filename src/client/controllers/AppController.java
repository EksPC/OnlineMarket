package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

/**This class handles the button of the application and is called to display contents in the 
 * application centre.*/
public class AppController implements Initializable{
	
	private MainController controller;
	/**Pages:
	 * - 1: Products list
	 * - 2: Owned products
	 * - 3: Upload*/
	private int pageNumber = 1;
	private String currentId;
	
	@FXML
	private Label userNameField;
	@FXML
	private ScrollPane centrePane;
	@FXML
	private Button quitBtn;
	@FXML
	private Button productsListBtn;
	@FXML
	private Button returnProductsBtn;
	@FXML
	private Button uploadProductsBtn;
	@FXML
	private Button multiPurposeButton;
	
	
	@FXML
	/**This method triggers the main controller to quit the application.
	 * */
	private void quitButtonAction(final ActionEvent event) {
		controller.quit();
	}
	
	/**
	 *This method regulates the left-bar button named "Products" and 
	 *displays the list of forSaleProducts*/
	@FXML
	private void productsListButtonAction(final ActionEvent event) {
		pageNumber = 1;
		setMultiPurposeBtn(0);
		controller.showProducts(1);
	}
	
	//list of owned product event
	@FXML
	/**This method triggers the main controller to display the list of products for sale.
	 * */
	private void returnProductsButtonAction(final ActionEvent event) {
		pageNumber = 2;
		setMultiPurposeBtn(0);
		controller.showProducts(2);
		
	}
	
	//upload a product
	@FXML
	/**This method triggers the main controller to display the uploader screen (screen where you can upload new products).
	 * */
	private void uploadProductButtonAction(final ActionEvent event) {
		pageNumber = 3;
		setMultiPurposeBtn(0);
		controller.showUploader();
	}
	
	@FXML
	/**This method handles the {@code multiPurposeButton} click by detecting the page:
	 * if the current page is 1 (Products list) the button triggers controller's buy method.
	 * If current page is 2 (Owned products) the button triggers controller's return method.
	 * */
	private void multiPurposeButtonAction(final ActionEvent event) {
		if(pageNumber == 1) {
			controller.buyProduct(currentId);
		} else {
			controller.returnProduct(currentId);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	/**Constructor*/
	public AppController(MainController mainController) {
		this.controller = mainController;
	}
	
	/**This is the setter method of the field {@code centrePane} witch represents the scroll pane in the app's centre.*/
	public void setCentralPane(ScrollPane pane) {
		centrePane = pane;
	}
	
	/**Setter method of the user name field, on the left top of the application.*/
	public void setUserName(String txt) {
		userNameField.setText(txt);
	}

	/**This is the getter method for the field {@code pageNumber}, useful to display buy and return buttons.*/
	public int getPageNumber() {
		return this.pageNumber;
	}
	
	/**This method displays the BUY / RETURN button based on the page.
	 * It takes a String id as input, that represents the id of the selected product so if the BUY/RETURN button
	 * is clicked, this product is bought / returned.*/
	public void displayMultiPurposeBtn(String id) {
		setMultiPurposeBtn(pageNumber);
		currentId = id;
	}
	
	/**This method creates the BUY or RETURN button that appears on the application left bar:
	 * - code = 0: invisible button
	 * - code = 1: green button for buy
	 * - code = 2: pink button for return*/
	private void setMultiPurposeBtn(int code) {
		
		switch(code) {
		case 0:
			multiPurposeButton.setStyle("-fx-background-color: transparent");
			multiPurposeButton.setText("");
			multiPurposeButton.setOnMouseEntered(e ->multiPurposeButton.setStyle("-fx-background-color: transparent"));
			multiPurposeButton.setOnMouseExited(e ->multiPurposeButton.setStyle("-fx-background-color: transparent"));
			break;
		case 1:
			multiPurposeButton.setStyle("-fx-background-color: green");
			multiPurposeButton.setOnMouseEntered(e -> multiPurposeButton.setStyle("-fx-background-color: rgba(0,128,0,0.75)"));
			multiPurposeButton.setOnMouseExited(e -> multiPurposeButton.setStyle("-fx-background-color: green"));
			multiPurposeButton.setText("BUY");
			break;
			
		case 2:
			multiPurposeButton.setOnMouseEntered(e -> multiPurposeButton.setStyle("-fx-background-color: rgba(255, 192, 203,0.75)"));
			multiPurposeButton.setOnMouseExited(e -> multiPurposeButton.setStyle("-fx-background-color: pink"));
			multiPurposeButton.setStyle("-fx-background-color: pink");
			multiPurposeButton.setText("RETURN");
			break;
		}
		
	}
}