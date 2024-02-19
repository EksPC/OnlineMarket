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
	private void uploadSingleProduct(final ActionEvent event) {
		String name = nameField.getText();		
		Double price = Double.parseDouble(priceField.getText());
		String id = idField.getText();
		
		Product newProduct = new Product(id, name, price);
//		newProduct.setOwnerName(controller.getCredentials().getUsr());
		
		warningText.setText("Uploading...");
		controller.uploadProduct(newProduct);
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public StackPane getUploader() {
		return this.uploader;
	}
	
	public UploaderController(MainController main) {
		this.controller = main;
	}
	
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
