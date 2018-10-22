package walletSeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class PushtoLocal {

	static InputStreamReader reader = new InputStreamReader(System.in);
    static BufferedReader br = new BufferedReader(reader);
    static String users = System.getProperty("user.name");
    
    static File file = new File("FloData.txt");
     
	public static void PushtoFile(String user, String pass) 
    {
    	String data = user + "#" + pass;
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //file.setReadOnly();
	}

}
