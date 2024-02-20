package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import client.controllers.MainController;
import entities.Product;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	
	
	@FXML
	/**
	 * This method handles the product click:
	 * If the client is in the "Products" section, a click allows him to buy a product.
	 * If the client is in the "Return" section, a click allows him to return a product.*/
	private void handleProductClick() {
		
	}
	
	private MainController controller;	
	
	/***/
	public SingleProductController(Product product, MainController contr) {
		nameField.setText(product.getName());
		idField.setText(product.getId());
		priceField.setText(Double.toString(product.getPrice()));
		
		if(product.getOwnerName() != "") {
			ownerText.setText("owner");
			ownerField.setText(product.getOwnerName());
		}
		this.controller = contr;
	}

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
