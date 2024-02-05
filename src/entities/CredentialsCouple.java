package entities;

import java.io.Serializable;

/**
 * This class represents a couple of credentials
 * username-password
 * */
public class CredentialsCouple implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	
	public CredentialsCouple(String usr, String passwd){
		this.password = passwd;
		this.username = usr;
	}
	
	public String getPasswd() {
		return this.password;
	}
	
	public String getUsr() {
		return this.username;
	}
}