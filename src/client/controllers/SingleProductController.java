package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
/*
 * TODO
 * - owner field
 * */
import javafx.scene.text.Text;


/**
 * This class handles the creation of a single product.
 * A single product is a {@code StackPane} with attributes inside.*/
public class SingleProductController implements Initializable{

	@FXML 
	public Text nameField;
	@FXML
	public Text idField;
	@FXML
	public Text priceField;
	@FXML
	public Text ownerText;
	@FXML 
	public Text ownerField;
		
	/***/
	public SingleProductController(Product product) {
		nameField.setText(product.getName());
		idField.setText(product.getId());
		priceField.setText(Double.toString(product.getPrice()));
		
		if(product.getOwnerName() != "") {
			ownerText.setText("owner");
			ownerField.setText(product.getOwnerName());
		}
	}

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
