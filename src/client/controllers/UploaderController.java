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
		
		Product newProduct = new Product(name, id, price);
		newProduct.setOwnerName(controller.getCredentials().getUsr());
		
//		controller.uploadProduct(newProduct);
		warningText.setText("Upload simulation.");
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
	
	
}
