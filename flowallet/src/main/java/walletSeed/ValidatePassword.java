package walletSeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ValidatePassword {

	static String users = System.getProperty("user.name");
	static File file = new File("FloData.txt");
	
	public static boolean myHelper(String user, String password) {
	    // This will reference one line at a time
		//String data = user + "#" + password;
	    String line = null;
		boolean retVal= false;
	    try {
	        // FileReader reads text files in the default encoding.
	    	FileReader fileReader = new FileReader(file);

	    	// Always wrap FileReader in BufferedReader.
	    	BufferedReader bufferedReader = new BufferedReader(fileReader);
	    	System.out.println("User::"+user);
	        while((line = bufferedReader.readLine()) != null) {
	            //create a token based on 
	        	System.out.println("data: "+line);
	        	
	            String [] token=line.split("#");
	            // because you know first and second word of each line in 
	            // given file is id and password 
	            if (/*token[0].equals(user) &&*/ token[1].equals(password)){
	                retVal=true;
	                System.out.println("Returned the value, the userId and password matches");
	                return retVal;
	            }
	        }   

	        // Always close files.
	        bufferedReader.close();         
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println(
	            "Unable to open file '" + 
	            "'");                
	    }
	    catch(IOException ex) {
	        System.out.println(
	            "Error reading file '" 
	            + "'");                  
	        // Or we could just do this: 
	        // ex.printStackTrace();
	    }
	    return retVal;
	}
}
