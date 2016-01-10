package roart.classification;

import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;
import roart.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpennlpClassify {

    private static Logger log = LoggerFactory.getLogger(OpennlpClassify.class);

    private static Map<String, DocumentCategorizerME> categorizerMap = null;
    
    public OpennlpClassify() {
	try {
		categorizerMap = new HashMap<String, DocumentCategorizerME>();
		String[] languages = LanguageDetect.getLanguages();
	    String modelFilePath = MyConfig.conf.opennlpmodelpath; 
	    for (String lang : languages) {
	    	String path = new String(modelFilePath);
	    	path = path.replaceFirst("LANG", lang);
	    	InputStream is = new FileInputStream(path);
	    	DoccatModel model = new DoccatModel(is);
	    	DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
	    	categorizerMap.put(lang, myCategorizer);
	    }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static String classify(String type, String language) {
    	DocumentCategorizerME myCategorizer = categorizerMap.get(language);
	double[] outcomes = myCategorizer.categorize(type);
	String category = myCategorizer.getBestCategory(outcomes);
	log.info("opennlp cat " + category);
	return category;
    }


}

