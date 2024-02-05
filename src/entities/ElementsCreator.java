package entities;

import java.io.IOException;

import client.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class ElementsCreator {
	
	private FXMLLoader productCellLoader;	
	private FXMLLoader productListLoader;
	private FXMLLoader uploadViewLoader;	
	
	
	
	public ElementsCreator() {
		productCellLoader = new FXMLLoader(Main.class.getResource("/product.fxml"));
		productListLoader = new FXMLLoader(Main.class.getResource("productsList.fxml"));
	}
	
	
	public void getProductCell(Product prod) {
		try {
			VBox product = productCellLoader.load();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
