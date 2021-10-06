package roart.classification.mahout.spark;

import roart.classification.MachineLearningAbstractClassifier;
import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorResult;
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
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MahoutSparkClassify extends MachineLearningAbstractClassifier implements java.io.Serializable {

	private static Logger log = LoggerFactory.getLogger(MahoutSparkClassify.class);

	private MahoutSparkConfig conf;

	public MahoutSparkClassify(String nodename, NodeConfig nodeConf) {
		try {
			conf = new MahoutSparkConfig();
			conf.dictionaryMap = new HashMap<String, Map<String, Integer>>();
			conf.documentFrequencyMap = new HashMap<String, Map<Integer, Long>>();
			conf.classifier2Map = new HashMap<String, ComplementaryNBClassifier>();
			conf.classifierMap = new HashMap<String, StandardNBClassifier>();
			conf.labelsMap = new HashMap<String, Map<Integer, String>>();
			conf.documentCountMap = new HashMap<String, Integer>();


			String basepath = nodeConf.getMahoutSparkBasePath();
			if (basepath == null) {
				basepath = "";
			}
			boolean testComplementary = false;
			String modelPath = basepath + nodeConf.getMahoutSparkModelPath();
			//String labelIndexPath = conf.mahoutlabelindexpath;
			String dictionaryPath = basepath + nodeConf.getMahoutSparkDictionaryPath();
			String documentFrequencyPath = basepath + nodeConf.getMahoutSparkDocumentFrequencyPath();
			String bayestype = basepath + nodeConf.getMahoutSparkAlgorithm();
			String sparkmaster = basepath + nodeConf.getMahoutSparkMaster();
			// not waterproof on purpose, won't check if var correctly set	    
			conf.bayes = "bayes".equals(bayestype);

			Configuration configuration = new Configuration();
			String fsdefaultname = nodeConf.getMahoutSparkConfFs();
			if (fsdefaultname != null && !fsdefaultname.isEmpty()) {
				configuration.set("fs.default.name", fsdefaultname);
			}
			String[] languages = nodeConf.getLanguages();
			for (String lang : languages) {
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
						"file:" + userDir + "/deps/mahout-spark_2.10-0.12.0.jar", 
						"file:" + userDir + "/deps/mahout-hdfs-0.12.0.jar", 
						"file:" + userDir + "/deps/mahout-math-0.12.0.jar", 
						"file:" + userDir + "/deps/mahout-math-scala_2.10-0.12.0.jar", 
						"file:" + userDir + "/deps/guava-16.0.1.jar", 
						"file:" + userDir + "/deps/fastutil-7.0.11.jar", 
						"file:" + userDir + "/aether-mahout-spark-0.10-SNAPSHOT.jar", 
				};
				sparkconf.setJars(jars);
				JavaSparkContext jsc = new JavaSparkContext(sparkconf);
				conf.jsc = jsc;
				SparkDistributedContext sdc = new SparkDistributedContext(jsc.sc());
				DistributedContext dc = sdc;
				conf.nbm = NBModel.dfsRead(modelPath.replaceAll("LANG", lang), dc);
				NBModel model = conf.nbm;
				if (ConfigConstants.CBAYES.equals(bayestype)) {
					classifier2 = new ComplementaryNBClassifier(model);
				}
				if (ConfigConstants.BAYES.equals(bayestype)) {
					classifier = new StandardNBClassifier( model) ;
				}
				conf.classifierMap.put(lang, classifier);
				conf.classifier2Map.put(lang, classifier2);

				scala.collection.Map<String, Integer> labels = null;
				JavaPairRDD<Text, IntWritable> dictionaryRDDSpark = null;
				JavaPairRDD<IntWritable, LongWritable> documentFrequencyRDDSpark = null;
				int documentCount = 0;
				
				dictionaryRDDSpark = jsc.sequenceFile(dictionaryPath.replaceAll("LANG", lang), Text.class, IntWritable.class);
				JavaPairRDD<String, Integer> dictionaryRDD = dictionaryRDDSpark.mapToPair(new ConvertToNativeTypes());
				//JavaPairRDD<String, Integer> dictionaryRDD = dictionaryRDDSpark.mapToPair(t -> new Tuple2<String, Integer>(t._1.toString(), t._2$mcI$sp()));
				Map<String, Integer> dictionary = dictionaryRDD.collectAsMap();

				documentFrequencyRDDSpark = jsc.sequenceFile(documentFrequencyPath.replaceAll("LANG", lang), IntWritable.class, LongWritable.class);
				JavaPairRDD<Integer, Long> documentFrequencyRDD = documentFrequencyRDDSpark.mapToPair(new ConvertToNativeTypes2());
				Map<Integer, Long> documentFrequency = documentFrequencyRDD.collectAsMap();

				labels = conf.nbm.labelIndex();
				Map<Integer, String> labelsSwapMap = new HashMap<>();
				scala.collection.Set<String> keySetScala = labels.keySet();
				Set<String> keySet = JavaConversions.setAsJavaSet(keySetScala);
				for(String key : keySet){
					scala.Option<Integer> value = labels.get(key);
					if (value != null) {
						labelsSwapMap.put(new Integer(value.get()), key);
					}
				}

				conf.labelsMap.put(lang, labelsSwapMap);
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
		conf.jsc.stop();
		// TODO propagate
		return null;
	}

	public MachineLearningClassifyResult classify(MachineLearningClassifyParam classify) {
		String content = classify.str;
		String language = classify.language;
		try {
			Map<String, Integer> dictionary = conf.dictionaryMap.get(language);
			Map<Integer, Long> documentFrequency = conf.documentFrequencyMap.get(language);
			ComplementaryNBClassifier classifier2 = conf.classifier2Map.get(language);
			StandardNBClassifier classifier = conf.classifierMap.get(language);
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
				//log.info(" " + labels.get(categoryId) + ": " + score);
			}
			log.info(" cat " + labels.get(bestCategoryId));
			MachineLearningClassifyResult result = new MachineLearningClassifyResult();
			result.result = labels.get(new Integer(bestCategoryId));
			return result;
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

