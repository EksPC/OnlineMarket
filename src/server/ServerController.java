package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import entities.CredentialsCouple;
import entities.Product;

/**This class handles some operations needed by the {@code MarketServer}.
 * - Storage access and handling (credentials and forSaleProducts)
 * - Credentials checking
 * - 
 * 
 * @author EksPC*/
public class ServerController {
	
	
	private static List<CredentialsCouple> credentials = new ArrayList<>();
	private static List<Product> products = new ArrayList<>();
	private static List<Product> ownedProducts = new ArrayList<Product>();
	private static List<Product> forSaleProducts = new ArrayList<>();
 	
	private static final String credentialsFileName = "resources/credentials.txt";
	private static final String storageFileName = "resources/storage.txt";
	
	private static CredentialsCouple userCredentials;
	public ServerController() {
		getProductsFromFile();
		getCredentialsFromFile();
		
	}
	
	
	/** 
	 * This method checks whether a user is in the database or not
	 * and returns true or false, respectively.
	 * 
	 * @param cred: {@code CredentialsCouple} object
	 * */
	public boolean checkCredentials(CredentialsCouple cred) {

		for(CredentialsCouple c : credentials) {
			if(c.getPasswd().equals(cred.getPasswd()) && c.getUsr().equals(cred.getUsr())) {
				userCredentials = c;
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**This method returns the requested list of products.
	 * Code 1 = products for sell.
	 * Code 2 = owned products.
	 * 
	 * @param code - int
	 * @return List<Product> 
	 * */
	public List<Product> getProductsList(int code){
		
		if(code == 0) {
			return products;
		
		} else if(code == 1) {
		
			return forSaleProducts;
		} 
		
		return ownedProducts;
	}
	
	
	/**This method updates the two available products list (products for sale and products owned by the client).*/
	public void updateProductsLists() {
		List<Product> newFS = new ArrayList<Product>();
		List<Product> newO = new ArrayList<Product>();
		
		for(Product prod:products) {
			if(prod.getOwnerName().equals(userCredentials.getUsr())) {
				newO.add(prod);
			} else {
				newFS.add(prod);
			}
		}
		
		ownedProducts = newO;
		forSaleProducts = newFS;
	}
	
	
	
	/**
	 * This method gets the forSaleProducts from a file and stores them
	 * in a local list using the {@code makeProduct} method.
	 * 
	 * @see makeProduct
	 * */
	private static void getProductsFromFile() {
		
		String line;
		Scanner scanner =  null;
		Product tmp;
		
	    try {
	        // Initialise InputStreamReader and BufferedReader
	        scanner = new Scanner(new File(storageFileName));

	        // Read the content from the InputStream
	        while (scanner.hasNextLine()) {
	        	line = scanner.nextLine();
	        	tmp = makeProduct(line);
	        	products.add(tmp);
	        	
	        }
	    } catch (IOException e) {
	    	System.out.println(e.getLocalizedMessage());
	    } finally {
	        // Close resources in the finally block to ensure they are always closed
	    	if(scanner != null) {
	    		scanner.close();
	    	}
	        
	    }
	}
	
	
	/** 
	 * This method transforms a string line taken from the product file
	 * into a Product object.
	 * 
	 * @see Product
	 * */ 
	private static Product makeProduct(String line) {
		
		//Variables definition
		String name, id;
		String owner = new String();
		Double price = 0.0;
		
		//Delimiters for file-line handling
		int firstDelimiter = line.indexOf(';');
		int secondDelimiter = line.indexOf(';',firstDelimiter+1);
		int thirdDelimiter = line.indexOf(';',secondDelimiter+1);
			
		//file-line handling
		id = line.substring(0,firstDelimiter);
		name = line.substring(firstDelimiter+1,secondDelimiter);
		
		//If there's no owner:
		// price will be extracted from secondDelimiter to end of line
		if(thirdDelimiter == -1) {
			thirdDelimiter = line.length();
		} else {
			owner = line.substring(thirdDelimiter+1);
		}
		
		try {
			price = Double.parseDouble(line.substring(secondDelimiter+1,thirdDelimiter));
			
		} catch (NumberFormatException ne) {
//			logger.log(Level.SEVERE,"Conversion error (parseInt) -> Message:\n" + ne.getMessage());
			return null;
		}
		
		Product prod = new Product(name,id,price);
		
		if(owner != null) {
			prod.setOwnerName(owner);
		}
		return prod;
	}

	
	
	/**
	 * This function reads the credentials file and stores every couple (username - password) in
	 * an arrayList.
	 * */
	private static void getCredentialsFromFile() {	
		
		
		String usr,passwd,line;
		int delimiter;
		Scanner scanner = null;
		File file = null;
		
		try {
			//file object
			file = new File(credentialsFileName);
			scanner = new Scanner(file);
			
			//file read
            while (scanner.hasNextLine()) {
            	
            	line = scanner.nextLine().trim();
            	delimiter = line.indexOf(';');
				usr = line.substring(0,delimiter);
				passwd = line.substring(delimiter+1);
				
				credentials.add(new CredentialsCouple(usr,passwd));
            }
            
        } catch (IOException e) {
//           logger.log(Level.SEVERE,"Credentials reading error - "+e.getLocalizedMessage());
           
        } finally {
            if (scanner != null) {
			    scanner.close();
			}
        } 
	}	


	/**
	 * This method upload a product (if the id is valid) by weriting it on the database.
	 * @return boolean*/
	public boolean uploadProduct(String prod) throws IOException {
		
		if(prod == null) {
			return false;
		}
		
		Product newProd = makeProduct(prod);
		
		if(!checkUpload(newProd)) {
			return false;
		}
		
		
		products.add(newProd);
		updateProductsLists();
		
		return true;
	}
	
	
	/**This method uploads the storage file at the end of every client session by fully re-writing
	 * the storage file.*/
	public void updateProductsFile() throws IOException{
		
		FileWriter fWriter = new FileWriter(storageFileName);
		BufferedWriter writer = new BufferedWriter(fWriter);

		for(Product p : products) {
			writer.write(p.toString());
			writer.newLine();
		}
		writer.close();
	}
	
	
	/**
	 * This method return a boolean. It returns {@code true} if the ID of the new product is not inside of the database, 
	 * {@code false} else. The algorithm runs in O(n).
	 * 
	 * @return boolean*/
	private boolean checkUpload(Product prod) {
		for(Product p : products) {
			if(p.getId().equals(prod.getId())) {
				return false;
			}
		}
		return true;
	}

	/**This method allows a user to buy a product. Basically it re-writes the ownership of a product and updates the lists
	 * of product with the method {@code updateProductsLists}.*/
	public boolean buyProduct(String id) {
		for(int i = 0; i < products.size();i++){
			if(products.get(i).getId().equals(id)) {
				
				products.get(i).setOwnerName(userCredentials.getUsr());
				updateProductsLists();
				return true;
			}
		}
		return false;
	}	
	
	
	/**This method allows a user to return a product owned by the current user.*/
	public boolean returnProduct(String id) {
		for(int i = 0; i < products.size();i++){
			if(products.get(i).getId().equals(id)) {
				products.get(i).setOwnerName("");
				updateProductsLists();
				return true;
			}
		}
		return false;
	}
	
}