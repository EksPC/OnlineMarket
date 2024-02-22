package client.controllers;


import java.io.IOException;
import java.util.List;
import entities.Product;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;




/**This class creates the products list screen (FOR SALE and YOUR PRODUCTS).*/
public class ProductsController{
	
	private String productPath = "/fxml/product.fxml";
	private MainController controller;
	private StackPane selected = new StackPane();
	
	public ProductsController(MainController cont){
		controller = cont;
	}
	
	/**This method creates a VBox object and returns it. The VBox contains multiple HBoxes,
	 * each of them containing 3 StackPane created using the {@code buildSingleProduct} function.*/
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
	
	

		
	
	
	/**This method creates a StackPane based on a Product objects parameters, then returns it.*/
	private StackPane buildSingleProduct(Product prod, StackPane productPane) {
		
		Label name = (Label) productPane.lookup("#nameField");
		name.setText(prod.getName());
		
		Text id = (Text) productPane.lookup("#idField");
		id.setText(prod.getId());
		
		Label price = (Label) productPane.lookup("#priceField");
		price.setText("$"+Double.toString(prod.getPrice()));
		
		productPane.setOnMouseClicked((MouseEvent event) -> {
			handleProductClick(id.getText());
			if(productPane == selected) {
				return;
			}
			selected.setStyle("-fx-background-color: white");
			productPane.setStyle("-fx-background-color: rgba(102, 153, 204, 0.8)");
			selected = productPane;
		});
		return productPane;
	}
	
	
	/**This method makes the main controller show the buy/return button in the left buttons section.
	 * It accepts an ID parameter needed to buy or return the specific product.
	 * */
	private void handleProductClick(String id) {
		controller.productClicked(id);
	}
	
	
}
