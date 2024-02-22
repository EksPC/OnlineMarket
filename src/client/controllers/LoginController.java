package client.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class LoginController implements Initializable{	
	
	private MainController controller;
	
	@FXML
	public TextField username_input;
	
	@FXML
	public PasswordField password_input;
	
	@FXML
	public Button submit_btn;

	@FXML
	public Text warning_text;
	
	@FXML 
	private void handleSubmitButtonAction(final ActionEvent event) {
//		logger.log(Level.FINE,"CONTROLLER - buttonClicked execution");
		String username = username_input.getText();
		String password = password_input.getText();
		
		password_input.setText("");
		username_input.setText("");
		
		controller.checkLogin(username, password);
	}
	
	/**Constructor*/
	public LoginController(MainController cont) {
		this.controller = cont;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	
	public void setWarningText(String message) {
		warning_text.setText(message);
	}
	
}
