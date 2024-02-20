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
	/**Pages:
	 * - 1: Products list
	 * - 2: Owned products
	 * - 3: Upload*/
	private int pageNumber = 1;
	private String currentId;
	
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
	private Button multiPurposeButton;
	
	
	@FXML
	/**This method triggers the main controller to quit the application.
	 * */
	private void quitButtonAction(final ActionEvent event) {
		warningText.setText("closing app...");
		controller.quit();
	}
	
	/**
	 *This method regulates the left-bar button named "Products" and 
	 *displays the list of forSaleProducts*/
	@FXML
	private void productsListButtonAction(final ActionEvent event) {
		pageNumber = 1;
		warningText.setText("Displaying products...");
		setMultiPurposeBtn(0);
		controller.showProducts(1);
	}
	
	//list of owned product event
	@FXML
	/**This method triggers the main controller to display the list of products for sell.
	 * */
	private void returnProductsButtonAction(final ActionEvent event) {
		pageNumber = 2;
		warningText.setText("Displaying owned products...");
		setMultiPurposeBtn(0);
		controller.showProducts(2);
		
	}
	
	//upload a product
	@FXML
	/**This method triggers the main controller to display the uploader screen.
	 * */
	private void uploadProductButtonAction(final ActionEvent event) {
		pageNumber = 3;
		setMultiPurposeBtn(0);
		warningText.setText("");
		controller.showUploader();
	}
	
	@FXML
	private void multiPurposeButtonAction(final ActionEvent event) {
		if(pageNumber == 1) {
			controller.buyProduct(currentId);
		} else {
			controller.returnProduct(currentId);
		}
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
	
	public int getPageNumber() {
		return this.pageNumber;
	}
	
	
	public void displayMultiPurposeBtn(String id) {
		setMultiPurposeBtn(pageNumber);
		currentId = id;
	}
	
	/**This method creates a button based on the environment:
	 * - code = 0: invisible button
	 * - code = 1: green button for buy
	 * - code = 2: pink button for return*/
	private void setMultiPurposeBtn(int code) {
		
		switch(code) {
		case 0:
			multiPurposeButton.setStyle("-fx-background-color: transparent");
			multiPurposeButton.setText("");
			break;
		case 1:
			multiPurposeButton.setStyle("-fx-background-color: green");
			multiPurposeButton.setOnMouseEntered(e -> multiPurposeButton.setStyle("-fx-background-color: rgba(0,128,0,0.75)"));
			multiPurposeButton.setOnMouseExited(e -> multiPurposeButton.setStyle("-fx-background-color: green"));
//			multiPurposeButton.setOnMouseClicked((MouseEvent)-> {
//				multiPurposeButtonAction();
//			});
			multiPurposeButton.setText("BUY");
			break;
			
		case 2:
			multiPurposeButton.setOnMouseEntered(e -> multiPurposeButton.setStyle("-fx-background-color: rgba(255, 192, 203,0.75)"));
			multiPurposeButton.setOnMouseExited(e -> multiPurposeButton.setStyle("-fx-background-color: pink"));
			multiPurposeButton.setStyle("-fx-background-color: pink");
//			multiPurposeButton.setOnMouseClicked((MouseEvent)-> {
//				multiPurposeButtonAction();
//			});
			multiPurposeButton.setText("RETURN");
			break;
		}
		
	}
}