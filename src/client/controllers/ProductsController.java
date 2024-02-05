package client.controllers;

import java.io.IOException;
import java.util.List;

import entities.Product;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

//NOTE
/*
 * ScrollPane con HBox sopra in cui in ogni */


/**This class handles the forSaleProducts list screen.
 * It creates and update the list of {@code Products}.*/
public class ProductsController{
	
	private ScrollPane root = new ScrollPane();
	private String productPath = "/fxml/product.fxml";
	
	private List<Product> products;
	
	private StackPane lastHandled;
	private MainController controller;
	
	public ProductsController(MainController cont){
		this.controller = cont;
	}
	
	/**This method creates a VBox object and returns it. The VBox contains multiple HBoxes, each of them containing
	 * 3 StackPane created using the {@code buildSingleProduct} function.*/
	public VBox getProductsView(List<Product> products){
		
		HBox tmp = new HBox();
		VBox total = new VBox();
		int index = 0;
		
		for(Product prod : products) {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(productPath));
			StackPane productPane = null;
			
			try {
				productPane = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.getLocalizedMessage();
			}
			
			
			productPane = buildSingleProduct(prod, productPane);
			
			if(index%3 == 0) {
				index = 0;
				total.getChildren().add(tmp);
				tmp = new HBox();
			}
			
			tmp.getChildren().add(productPane);
			index++;
		}
		
		if(!tmp.getChildren().isEmpty()) {
			total.getChildren().add(tmp);
		}
		
		return total;
		
	}
	
	

		
	
	
	/**This method creates a StackPane representing a Product object, then returns it.*/
	private StackPane buildSingleProduct(Product prod, StackPane productPane) {
		
		Text owner = (Text) productPane.lookup("#ownerField");
		Label ownerTxt = (Label) productPane.lookup("#ownerTypeField");
		
		Label name = (Label) productPane.lookup("#nameField");
		name.setText(prod.getName());
		
		Text id = (Text) productPane.lookup("#idField");
		id.setText(prod.getId());
		
		Label price = (Label) productPane.lookup("#priceField");
		price.setText("$"+Double.toString(prod.getPrice()));
		
		if(prod.hasOwner()) {
			
			owner.setText(prod.getOwnerName());
			ownerTxt.setText("Owner: ");
		
		} else {
			ownerTxt.setText("");
			owner.setText("");
		}
		return productPane;
	}
	
	
}
