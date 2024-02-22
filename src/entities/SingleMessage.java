package entities;

import java.io.Serializable;

/**
 * The {@code MyMessage} class represents the single message between client and server.
 * It contains multiple fields, useful to specify connection state, authentication state, request code and 
 * a string.
 * */
public class SingleMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private boolean connection = false;
	private boolean authentication = false;
	
	private int request;	//CLIENT
	public String str;
	
	public SingleMessage(boolean connection, boolean authentication){
		this.connection = connection;
	}

	/**Getter method for the field */
	public boolean getConnection() {
		return this.connection;
	}
	
	/**Setter method for the field request.*/
	public void setRequest(int choice) {
		this.request = choice;
	}
	
	/**Getter method for the field request.*/
	public int getRequest() {
		return this.request;
	}
	/**Setter method for the field authentication.*/
	public void setAuthentication(boolean val) {
		this.authentication = val;
	}
	
	/**Getter method for the field authentication.*/
	public boolean getAuthentication() {
		return this.authentication;
	}
	
}