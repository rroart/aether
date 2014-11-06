package roart.classification;

import roart.model.ResultItem;
import roart.util.ConfigConstants;
import roart.util.Constants;

import java.util.List;

import java.io.InputStream;
import java.io.FileInputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpennlpClassify {

    private static Logger log = LoggerFactory.getLogger(OpennlpClassify.class);

    private static DoccatModel model = null;
    private static DocumentCategorizerME myCategorizer = null;

    public OpennlpClassify() {
	try {
	    String modelFilePath = roart.util.Prop.getProp().getProperty(ConfigConstants.OPENNLPMODELPATH);
	    InputStream is = new FileInputStream(modelFilePath);
	    model = new DoccatModel(is);
	    myCategorizer = new DocumentCategorizerME(model);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static String classify(String type) {
	double[] outcomes = myCategorizer.categorize(type);
	String category = myCategorizer.getBestCategory(outcomes);
	log.info("opennlp cat " + category);
	return category;
    }


}

