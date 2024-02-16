package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
	
	
	
	/**This method returns every product for sell.*/
	public List<Product> getProductsList(){
		return products;
	}
	
	/**This method returns (if exists) a list of {@code Product} object owned by a user specified by
	 * the input {@code ownerName}*/
	public List<Product> getOwnedProduct(String ownerName){
		
		List<Product> tmp = new ArrayList<Product>();
		
		for(Product prod : products) {
			if(prod.getOwnerName().equals(userCredentials.getUsr())) {
				tmp.add(prod);
			}
		}
		
		return tmp;
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
	
//	        logger.log(Level.FINE,"reading forSaleProducts");
	        // Read the content from the InputStream
	        while (scanner.hasNextLine()) {
	        	line = scanner.nextLine();
	        	tmp = makeProduct(line);
	        	products.add(tmp);
	        	
	        }
	    } catch (IOException e) {
//	        logger.log(Level.SEVERE,"Products reading error - "+e.getLocalizedMessage());
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
		int price = 0;
		
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
			price = Integer.parseInt(line.substring(secondDelimiter+1,thirdDelimiter));
			
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
	public boolean uploadProduct(Product newProd) throws IOException {
		
		if(!checkUpload(newProd)) {
			return false;
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(storageFileName));
		writer.write(newProd.toString());
		
		if(writer != null) {
			writer.close();
		}
		return true;
	}
	
	
	/**
	 * This method return a boolean. It returns {@code true} if the ID of the new product is not inside of the database, 
	 * {@code false} else. The algorithm runs in O(n).
	 * 
	 * @return boolean*/
	private boolean checkUpload(Product prod) {
		for(Product p : products) {
			if(p.getId() == prod.getId()) {
				return false;
			}
			
		}
		return true;
	}

}	
