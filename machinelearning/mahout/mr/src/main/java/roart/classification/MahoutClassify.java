package roart.classification;

import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorParam;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.config.ConfigConstants;
import roart.config.NodeConfig;
import roart.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.io.InputStream;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.ComplementaryNaiveBayesClassifier;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.common.nlp.NGrams;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MahoutClassify extends MachineLearningAbstractClassifier {

    private static Logger log = LoggerFactory.getLogger(MahoutClassify.class);

    private MahoutMRConfig conf;
    
    public MahoutClassify(String nodename, NodeConfig nodeConf) {
    	try {
    		conf = new MahoutMRConfig();
		conf.dictionaryMap = new HashMap<String, Map<String, Integer>>();
		conf.documentFrequencyMap = new HashMap<String, Map<Integer, Long>>();
		conf.classifier2Map = new HashMap<String, ComplementaryNaiveBayesClassifier>();
		conf.classifierMap = new HashMap<String, StandardNaiveBayesClassifier>();
	    conf.labelsMap = new HashMap<String, Map<Integer, String>>();
	    conf.documentCountMap = new HashMap<String, Integer>();
        String[] languages = nodeConf.getLanguages();
        
        
	    String basepath = nodeConf.getMahoutBasePath();
	    if (basepath == null) {
	    	basepath = "";
	    }
        String modelPath = nodeConf.getMahoutModelPath();
        String labelIndexPath = nodeConf.getMahoutLabelIndexPath();
        String dictionaryPath = nodeConf.getMahoutDictionaryPath();
        String documentFrequencyPath = nodeConf.getMahoutDocumentFrequencyPath();
        String bayestype = nodeConf.getMahoutAlgorithm();
	    // not waterproof on purpose, won't check if var correctly set	    
	    conf.bayes = "bayes".equals(bayestype);

	    Configuration configuration = new Configuration();
	    String fsdefaultname = nodeConf.getMahoutConfFs();
	    if (fsdefaultname != null && !fsdefaultname.isEmpty()) {
		configuration.set("fs.default.name", fsdefaultname);
	    }
        for (String lang : languages) {
            String path = new String(basepath);
            path = path.replaceAll("LANG", lang);
            NaiveBayesModel model = NaiveBayesModel.materialize(new Path(path + modelPath.replaceAll("LANG", lang)), configuration);	    
            ComplementaryNaiveBayesClassifier classifier2 = null;
            StandardNaiveBayesClassifier classifier = null;
            if (ConfigConstants.CBAYES.equals(bayestype)) {
            	classifier2 = new ComplementaryNaiveBayesClassifier(model);
            }
            if (ConfigConstants.BAYES.equals(bayestype)) {
            	classifier = new StandardNaiveBayesClassifier( model) ;
            }
            conf.classifierMap.put(lang, classifier);
            conf.classifier2Map.put(lang, classifier2);

            Map<Integer, String> labels = null;
            Map<String, Integer> dictionary = null;
            Map<Integer, Long> documentFrequency = null;
            int documentCount = 0;

            labels = BayesUtils.readLabelIndex(configuration, new Path(path + labelIndexPath.replaceAll("LANG", lang)));
            dictionary = readDictionnary(configuration, new Path(path + dictionaryPath.replaceAll("LANG", lang)));
            documentFrequency = readDocumentFrequency(configuration, new Path(path + documentFrequencyPath.replaceAll("LANG", lang)));
            conf.labelsMap.put(lang, labels);
            conf.dictionaryMap.put(lang, dictionary);
            conf.documentFrequencyMap.put(lang, documentFrequency);
            
            // analyzer used to extract word from content
            int labelCount = labels.size();
            log.info("Docs " + documentFrequency.size());
            // the -1 is in df-count/part-r-00000
            if (documentFrequency.get(-1) != null) {
            	documentCount = documentFrequency.get(-1).intValue();
            } else {
            	log.error("no size info in data");
            	documentCount = 1; // or try to just set one
            }
            conf.documentCountMap.put(lang, documentCount);
            log.info("Number of labels: " + labelCount);
            log.info("Number of documents in training set: " + documentCount);
        }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}

    }

	public MachineLearningConstructorResult destroy(String nodename) {
		return null;
	}
	
    public MachineLearningClassifyResult classify(MachineLearningClassifyParam classify) {
    		String content = classify.str;
    		String language = classify.language;
	try {
		Map<String, Integer> dictionary = conf.dictionaryMap.get(language);
		Map<Integer, Long> documentFrequency = conf.documentFrequencyMap.get(language);
		ComplementaryNaiveBayesClassifier classifier2 = conf.classifier2Map.get(language);
	    StandardNaiveBayesClassifier classifier = conf.classifierMap.get(language);
	    Map<Integer, String> labels = conf.labelsMap.get(language);
	    int documentCount = conf.documentCountMap.get(language);
	 	    Multiset<String> words = ConcurrentHashMultiset.create();
	    // extract words from content
	    int wordCount = getWords(content, dictionary, words);

	    // create vector wordId => weight using tfidf
	    Vector vector = new RandomAccessSparseVector(Integer.MAX_VALUE);
	    TFIDF tfidf = new TFIDF();
	    for (Multiset.Entry<String> entry:words.entrySet()) {
		String word = entry.getElement();
		int count = entry.getCount();
		Integer wordId = dictionary.get(word);
		Long freq = documentFrequency.get(wordId);
		double tfIdfValue = tfidf.calculate(count, freq.intValue(), wordCount, documentCount);
		vector.setQuick(wordId, tfIdfValue);
	    }
	    
	    Vector resultVector = null;
	    if (conf.bayes) {
		resultVector = classifier.classifyFull(vector);
	    } else {
		resultVector = classifier2.classifyFull(vector);
	    }		
	    double bestScore = -Double.MAX_VALUE;
	    int bestCategoryId = -1;
	    for(Element element: resultVector.all()) {
		int categoryId = element.index();
		double score = element.get();
		if (score > bestScore) {
		    bestScore = score;
		    bestCategoryId = categoryId;
		}
		log.info(" " + labels.get(categoryId) + ": " + score);
	    }
	    log.info(" cat " + labels.get(bestCategoryId));
	    MachineLearningClassifyResult result = new MachineLearningClassifyResult();
	    result.result = labels.get(bestCategoryId);
	    return result;
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return null;
    }

public static Map<String, Integer> readDictionnary(Configuration conf, Path dictionnaryPath) {
    Map<String, Integer> dictionnary = new HashMap<String, Integer>();
    for (Pair<Text, IntWritable> pair : new SequenceFileIterable<Text, IntWritable>(dictionnaryPath, true, conf)) {
	dictionnary.put(pair.getFirst().toString(), pair.getSecond().get());
    }
    return dictionnary;
}

public static Map<Integer, Long> readDocumentFrequency(Configuration conf, Path documentFrequencyPath) {
    Map<Integer, Long> documentFrequency = new TreeMap<Integer, Long>();
    for (Pair<IntWritable, LongWritable> pair : new SequenceFileIterable<IntWritable, LongWritable>(documentFrequencyPath, true, conf)) {
	documentFrequency.put(pair.getFirst().get(), pair.getSecond().get());
    }
    return documentFrequency;
}

    private static int getWords(String content, Map<String, Integer> dictionary, Multiset<String> words) {
	try {
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	TokenStream ts = analyzer.tokenStream("text", new StringReader(content));
	CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
	ts.reset();
	int wordCount = 0;
	while (ts.incrementToken()) {
	    if (termAtt.length() > 0) {
		String word = ts.getAttribute(CharTermAttribute.class).toString();
		Integer wordId = dictionary.get(word);
		// if the word is not in the dictionary, skip it
		if (wordId != null) {
		    words.add(word);
		    wordCount++;
		}
	    }
	}
	ts.close();
	return wordCount;
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return 0;
    }

}

