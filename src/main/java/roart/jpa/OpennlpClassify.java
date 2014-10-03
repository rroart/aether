package roart.jpa;

import roart.model.ResultItem;

import java.util.List;

import java.io.InputStream;
import java.io.FileInputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpennlpClassify {

    private static Log log = LogFactory.getLog("OpennlpClassify");

    private static DoccatModel model = null;
    private static DocumentCategorizerME myCategorizer = null;

    public OpennlpClassify() {
	try {
	    String modelFilePath = roart.util.Prop.getProp().getProperty("opennlpmodelpath");
	    InputStream is = new FileInputStream(modelFilePath);
	    model = new DoccatModel(is);
	    myCategorizer = new DocumentCategorizerME(model);
	} catch (Exception e) {
	    log.error("Exception", e);
	}
    }

    public static String classify(String type) {
	double[] outcomes = myCategorizer.categorize(type);
	String category = myCategorizer.getBestCategory(outcomes);
	log.info("opennlp cat " + category);
	return category;
    }


}

