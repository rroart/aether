package roart.classification;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.config.ConfigConstants;
import roart.model.ResultItem;

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
		// TODO propagate error
		classify.destructor();
	}
	if (true || classify == null) {
		// TODO make OO of this
	    if (type.equals(ConfigConstants.MAHOUT)) {
		classify = new MahoutClassifyAccess();
	    }
        if (type.equals(ConfigConstants.MAHOUTSPARK)) {
        classify = new MahoutSparkClassifyAccess();
        }
        if (type.equals(ConfigConstants.SPARKML)) {
        classify = new SparkMLClassifyAccess();
        }
	    if (type.equals(ConfigConstants.OPENNLP)) {
		classify = new OpennlpClassifyAccess();
	    }
	    // TODO propagate
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
