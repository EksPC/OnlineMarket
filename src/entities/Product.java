package entities;

import java.io.Serializable;

/**
 * This class represents a market product described by:
 * id, name, price.
 * It is possible that a {@code MyClient} already purchased the product:
 * 	in this case there's also the ownerName field;
 * 
 * @see MyClient
 * */
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String ownerName = "";
	private double price;
	
	
	public Product(String id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}
	
	/**Getter method that returns */
	public String getName() {
		return this.name;
	}
	
	/**Getter method that returns */
	public String getId() {
		return this.id;
	}
	
	/**Getter method that returns */
	public double getPrice() {
		return this.price;
	}
	
	/**Returns true if the product has an owner, false otherwise.*/
	public boolean hasOwner() {
		if(ownerName == "") {
			return false;
		} 
		return true;
	}
	
	/**Overriding of {@code toString} method.*/
	public String toString() {
		String toRet = name+";"+id+";"+Double.toString(price);
		if(ownerName != "") {
			toRet += ";"+ownerName;
		}
		return toRet;
	}
	
	/**Set the product price.*/
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}
	
	/**Setter method to set the owner method.*/
	public void setOwnerName(String name) {
		this.ownerName = name;
	}
	
	/**Getter method that returns the owner name.*/
	public String getOwnerName() {
		return this.ownerName;
	}
}
