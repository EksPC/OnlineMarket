package client;


import client.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



/**
 * This class displays the application to the user.
 * Every operation is handled by other related classes.*/
public class Main extends Application{

	private final MainController controller = new MainController(this);
	
	private Stage primaryStage;
	private BorderPane view;
	
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	
	/**Getter method for the scene root.*/
	public Parent getRoot() {
		return primaryStage.getScene().getRoot();
	}
	
	
	/**This method sets a new scene based on the input.*/
	public void setNewScene(BorderPane root) {
		
		view = root;
		
		Scene scene = new Scene(root);
			
		primaryStage.setScene(scene);
		primaryStage.setTitle("Online Market");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/**This method set a new centre of the main application.*/
	public void setNewCenter(ScrollPane centre1, StackPane centre2) {
		if(centre2 == null) {
			view.setCenter(centre1);
		} else {
			view.setCenter(centre2);
		}
		
	}
	
	
	/**
	 * This method closes every closable part of the application, then it closes the application
	 * itself. It is triggered when the {@code quit} button is pressed and it is invoked inside
	 * a controller.
	 * */
	public void quit() {
		System.out.println("App closing...");
		Platform.exit();
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		//Setting primary stage
		this.primaryStage = stage;
		
		//Initialisation of the first view - login
		Parent root = controller.getLoginScene();
		if(root == null) {
			return;
		}

		//scene display
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Online Marker - Login");
		primaryStage.show();
	}
	
	
	
}
