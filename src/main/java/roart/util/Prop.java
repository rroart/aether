package roart.util;

import java.util.Properties;
import java.io.*;

public class Prop {

    private static Properties prop = null;

    public static Properties getProp() {
	if (prop == null) {
	    prop = new Properties();
	    InputStream input = null;
	    
	    try {
		
		input = new FileInputStream("aether.prop");
		
		// load a properties file
		prop.load(input);
 
	    } catch (IOException ex) {
		ex.printStackTrace();
	    } finally {
		if (input != null) {
		    try {
			input.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return prop;
    }


}
