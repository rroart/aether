package roart.util;

import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Prop {

    private static final Logger LOG = LoggerFactory.getLogger(Prop.class);

    private static Properties properties = null;

    private Prop() {
    }

    // not used
    
    public synchronized static Properties getProp() {
        if (properties == null) {
            properties = new Properties();
            InputStream input = null;

            try {

                input = new FileInputStream("aether.prop");

                // load a properties file
                properties.load(input);

            } catch (IOException e) {
                LOG.error(Constants.EXCEPTION, e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        LOG.error(Constants.EXCEPTION, e);
                    }
                }
            }
        }
        return properties;
    }

}
