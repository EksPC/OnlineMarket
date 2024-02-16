package entities;

import java.io.Serializable;

/**
 * The {@code MyMessage} class represents a message that is sent from server to client
 * It helps 
 * */
public class SingleMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private boolean connection = false;
	private boolean authentication = false;
	
	private Object object;	//SERVER
	private int request;	//CLIENT
	public String str;
	
	public SingleMessage(boolean connection, boolean authentication){
		this.connection = connection;
	}

	
	public boolean getConnection() {
		return this.connection;
	}
	
	
	public void setRequest(int choice) {
		this.request = choice;
	}
	
	public int getRequest() {
		return this.request;
	}
	
	public void setAuthentication(boolean val) {
		this.authentication = val;
	}
	
	public boolean getAuthentication() {
		return this.authentication;
	}
	
	public void setObject(Object o) {
		this.object = o;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	/**
	 * The {@code getListOfCommands} function allows the server to send a client the 
	 * */
	
}