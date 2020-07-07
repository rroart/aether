package roart.classification;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.common.config.ConfigConstants;
import roart.common.constants.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyDao {
    private static Logger log = LoggerFactory.getLogger(ClassifyDao.class);

    private static ClassifyAccess classify = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (type == null) {
	  return;
	}
	if (classify != null) {
		try {
			classify.destructor();
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
	}
	if (true || classify == null) {
	    if (type.equals(ConfigConstants.MACHINELEARNINGMAHOUT)) {
		classify = new MahoutClassifyAccess();
	    }
        if (type.equals(ConfigConstants.MACHINELEARNINGMAHOUTSPARK)) {
        classify = new MahoutSparkClassifyAccess();
        }
        if (type.equals(ConfigConstants.MACHINELEARNINGSPARKML)) {
        classify = new SparkMLClassifyAccess();
        }
	    if (type.equals(ConfigConstants.MACHINELEARNINGOPENNLP)) {
		classify = new OpennlpClassifyAccess();
	    }
	    String error = classify.constructor();
	}
    }

    public static String classify(String type, String language) {
	if (classify == null) {
	    return null;
	}
	return classify.classify(type, language);
    }

}
