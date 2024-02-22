package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class UploaderController implements Initializable{

	private StackPane uploader;
	private MainController controller;
	
	@FXML
	private Text warningText;
	
	@FXML
	private TextField nameField;
	
	@FXML
	private TextField idField;
	
	@FXML
	private TextField priceField;
	
	@FXML 
	private Button uploadProductBtn;
	
	@FXML
	/**This method is the handler of the UPLOAD button in the section UPLOAD.
	 * When triggered, this handler takes the parameters in the uploader fields and, if valid,
	 * sends them to the controller to be stored.*/
	private void uploadSingleProduct(final ActionEvent event) {
			Product newProduct = null;
		try {
			String name = nameField.getText();		
			Double price = Double.parseDouble(priceField.getText());
			price = Math.floor(price * 100) / 100;
			String id = idField.getText();
			
			if(name.equals("") || price <= 0.0 || id.equals("")) {
				warningText.setText("PLEASE FILL ALL THE FIELDS!");
				return;
			}
			
			newProduct = new Product(id, name, price);
		} catch(NumberFormatException num) {
			warningText.setStyle("-fx-text-fill: rgba(0.8902, 0.5098, 0.0, 1.0)");
			warningText.setText("PLEASE, INSERT A VALID PRICE!");
			return;
		}
		warningText.setText("Uploading...");
		controller.uploadProduct(newProduct);
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public StackPane getUploader() {
		return this.uploader;
	}
	
	public UploaderController(MainController main) {
		this.controller = main;
	}
	
	/**This method shows the upload status:
	 * If upload was successful, a success message is print and the fields are clearesd.
	 * If upload was unsuccessful, an unsuccess message is print.  */
	public void printStatusMessage(boolean status) {
		
		if(status) {
			warningText.setText("Product uploaded succesfully!");
			warningText.setFill(Color.GREEN);
			
			nameField.setText("");
			idField.setText("");
			priceField.setText("");
			
		} else {
			warningText.setText("Upload error, invalid ID! \nThe ID you choose is already taken.");
		}
	}
	
	
}
