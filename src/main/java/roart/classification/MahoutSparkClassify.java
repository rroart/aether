package roart.classification;

import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.lang.LanguageDetect;
import roart.util.Constants;
import scala.Tuple2;
import scala.collection.JavaConversions;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.io.StringReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.drm.DistributedContext;
import org.apache.mahout.nlp.tfidf.TFIDF;
import org.apache.mahout.sparkbindings.SparkDistributedContext;
import org.apache.mahout.classifier.naivebayes.ComplementaryNBClassifier;
import org.apache.mahout.classifier.naivebayes.NBModel;
import org.apache.mahout.classifier.naivebayes.StandardNBClassifier;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MahoutSparkClassify {

    private static Logger log = LoggerFactory.getLogger(MahoutSparkClassify.class);
    
    private static Map<String, Map<String, Integer>> dictionaryMap = null;
    private static Map<String, Map<Integer, Long>> documentFrequencyMap = null;
    private static Map<String, ComplementaryNBClassifier> classifier2Map = null;
    private static Map<String, StandardNBClassifier> classifierMap = null;
    private static Map<String, Map<Integer, String>> labelsMap = null;
    private static Map<String, Integer> documentCountMap = null;

    private static NBModel nbm;
    
    private static boolean bayes = true;
    
    public MahoutSparkClassify() {
	try {
		dictionaryMap = new HashMap<String, Map<String, Integer>>();
	    documentFrequencyMap = new HashMap<String, Map<Integer, Long>>();
	    classifier2Map = new HashMap<String, ComplementaryNBClassifier>();
	    classifierMap = new HashMap<String, StandardNBClassifier>();
	    labelsMap = new HashMap<String, Map<Integer, String>>();
	    documentCountMap = new HashMap<String, Integer>();
        String[] languages = LanguageDetect.getLanguages();
        
        
	    String basepath = MyConfig.conf.mahoutbasepath;
	    if (basepath == null) {
	    	basepath = "";
	    }
	    boolean testComplementary = false;
        String modelPath = MyConfig.conf.mahoutmodelpath;
        //String labelIndexPath = MyConfig.conf.mahoutlabelindexpath;
        String dictionaryPath = MyConfig.conf.mahoutdictionarypath;
        String documentFrequencyPath = MyConfig.conf.mahoutdocumentfrequencypath;
        String bayestype = MyConfig.conf.mahoutalgorithm;
        String sparkmaster = MyConfig.conf.mahoutsparkmaster;
	    // not waterproof on purpose, won't check if var correctly set	    
	    bayes = "bayes".equals(bayestype);

	    Configuration configuration = new Configuration();
	    String fsdefaultname = MyConfig.conf.mahoutconffs;
	    if (fsdefaultname != null) {
		configuration.set("fs.default.name", fsdefaultname);
	    }
        for (String lang : languages) {
            String path = new String(basepath);
            path = path.replaceAll("LANG", lang);
            ComplementaryNBClassifier classifier2 = null;
            StandardNBClassifier classifier = null;
            SparkConf sparkconf = new SparkConf();
            String master = sparkmaster;
            sparkconf.setMaster(master);
            sparkconf.setAppName("aether");
            // it does not work well with default snappy
            sparkconf.set("spark.io.compression.codec", "lzf");
            sparkconf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
            sparkconf.set("spark.kryo.registrator", "org.apache.mahout.sparkbindings.io.MahoutKryoRegistrator");
            String userDir = System.getProperty("user.dir");
            log.info("user.dir " + userDir);
            String[] jars = { 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib/mahout-spark_2.10-0.12.0.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib//mahout-hdfs-0.12.0.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib/mahout-math-0.12.0.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib//guava-16.0.1.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib/fastutil-7.0.11.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib//mahout-math-scala_2.10-0.12.0.jar",
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT.jar" 
            };
            sparkconf.setJars(jars);
            //SparkContext sc = new SparkContext(sparkconf);
            JavaSparkContext jsc = new JavaSparkContext(sparkconf);
            SparkDistributedContext sdc = new SparkDistributedContext(jsc.sc());
            DistributedContext dc = sdc;
            nbm = NBModel.dfsRead(modelPath, dc);
            NBModel model = nbm;
            if (ConfigConstants.CBAYES.equals(bayestype)) {
            	classifier2 = new ComplementaryNBClassifier(model);
            }
            if (ConfigConstants.BAYES.equals(bayestype)) {
            	classifier = new StandardNBClassifier( model) ;
            }
            classifierMap.put(lang, classifier);
            classifier2Map.put(lang, classifier2);

            scala.collection.Map<String, Integer> labels = null;
            JavaPairRDD<Text, IntWritable> dictionaryRDDSpark = null;
            JavaPairRDD<IntWritable, LongWritable> documentFrequencyRDDSpark = null;
            int documentCount = 0;

            dictionaryRDDSpark = jsc.sequenceFile(dictionaryPath, Text.class, IntWritable.class);
            JavaPairRDD<String, Integer> dictionaryRDD = dictionaryRDDSpark.mapToPair(new ConvertToNativeTypes());
            Map<String, Integer> dictionary = dictionaryRDD.collectAsMap();
            
            documentFrequencyRDDSpark = jsc.sequenceFile(documentFrequencyPath, IntWritable.class, LongWritable.class);
            JavaPairRDD<Integer, Long> documentFrequencyRDD = documentFrequencyRDDSpark.mapToPair(new ConvertToNativeTypes2());
            Map<Integer, Long> documentFrequency = documentFrequencyRDD.collectAsMap();

            labels = nbm.labelIndex();
            Map<Integer, String> labelsSwapMap = new HashMap<>();
            scala.collection.Set<String> keySetScala = labels.keySet();
            Set<String> keySet = JavaConversions.asJavaSet(keySetScala);
            for(String key : keySet){
                scala.Option<Integer> value = labels.get(key);
                if (value != null) {
                    labelsSwapMap.put(new Integer(value.get()), key);
                }
            }

            labelsMap.put(lang, labelsSwapMap);
            dictionaryMap.put(lang, dictionary);
            documentFrequencyMap.put(lang, documentFrequency);
            
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
            documentCountMap.put(lang, documentCount);
            log.info("Number of labels: " + labelCount);
            log.info("Number of documents in training set: " + documentCount);
        }
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}

    }

    public static String classify(String content, String language) {
	try {
	    Map<String, Integer> dictionary = dictionaryMap.get(language);
	    Map<Integer, Long> documentFrequency = documentFrequencyMap.get(language);
		ComplementaryNBClassifier classifier2 = classifier2Map.get(language);
	    StandardNBClassifier classifier = classifierMap.get(language);
	    Map<Integer, String> labels = labelsMap.get(language);
	    int documentCount = documentCountMap.get(language);
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
	    if (bayes) {
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
		//log.info(" " + labels.get(categoryId) + ": " + score);
	    }
	    log.info(" cat " + labels.get(bestCategoryId));
	    return labels.get(new Integer(bestCategoryId));
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return null;
    }

    private static int getWords(String content, Map<String, Integer> dictionary, Multiset<String> words) {
	try {
	StandardAnalyzer analyzer = new StandardAnalyzer();
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

    public static class ConvertToNativeTypes implements PairFunction<Tuple2<Text, IntWritable>, String, Integer> {
        public Tuple2<String, Integer> call(Tuple2<Text, IntWritable> record) {
          return new Tuple2(record._1.toString(), record._2.get());
        }
      }

    public static class ConvertToNativeTypes2 implements PairFunction<Tuple2<IntWritable, LongWritable>, Integer, Long> {
        public Tuple2<Integer, Long> call(Tuple2<IntWritable, LongWritable> record) {
          return new Tuple2(record._1.get(), record._2.get());
        }
      }

}

