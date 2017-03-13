package roart.classification;

import java.util.Map;

import org.apache.mahout.classifier.naivebayes.ComplementaryNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;

public class MahoutMRConfig {
    public Map<String, Map<String, Integer>> dictionaryMap = null;
    public Map<String, Map<Integer, Long>> documentFrequencyMap = null;
    public Map<String, ComplementaryNaiveBayesClassifier> classifier2Map = null;
    public Map<String, StandardNaiveBayesClassifier> classifierMap = null;
    public Map<String, Map<Integer, String>> labelsMap = null;
    public Map<String, Integer> documentCountMap = null;

    public boolean bayes = true;    
}
