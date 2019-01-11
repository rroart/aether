package roart.classification.mahout.spark;

import java.util.Map;

import org.apache.mahout.classifier.naivebayes.ComplementaryNBClassifier;
import org.apache.mahout.classifier.naivebayes.NBModel;
import org.apache.mahout.classifier.naivebayes.StandardNBClassifier;
import org.apache.spark.api.java.JavaSparkContext;

public class MahoutSparkConfig implements java.io.Serializable {
	
    public Map<String, Map<String, Integer>> dictionaryMap = null;
    public Map<String, Map<Integer, Long>> documentFrequencyMap = null;
    public Map<String, ComplementaryNBClassifier> classifier2Map = null;
    public Map<String, StandardNBClassifier> classifierMap = null;
    public Map<String, Map<Integer, String>> labelsMap = null;
    public Map<String, Integer> documentCountMap = null;

    public NBModel nbm;
    
    public boolean bayes = true;
    
    public JavaSparkContext jsc;
}