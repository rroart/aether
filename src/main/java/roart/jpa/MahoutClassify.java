package roart.jpa;

import roart.model.ResultItem;

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
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.common.nlp.NGrams;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MahoutClassify {

    private static Log log = LogFactory.getLog("MahoutClassify");

    private static String modelPath = null;
    private static String labelIndexPath = null;
    private static String dictionaryPath = null;
    private static String documentFrequencyPath = null;

    private static StandardNaiveBayesClassifier classifier = null;
    private static Map<Integer, String> labels = null;
    private static Map<String, Integer> dictionary = null;
    private static Map<Integer, Long> documentFrequency = null;
    private static int documentCount = 0;

    public MahoutClassify() {
	try {
	    modelPath = roart.util.Prop.getProp().getProperty("mahaoutmodelpath");
	    labelIndexPath = roart.util.Prop.getProp().getProperty("mahoutlabelindexfilepath");
	    dictionaryPath = roart.util.Prop.getProp().getProperty("mahoutdictionarypath");
	    documentFrequencyPath = roart.util.Prop.getProp().getProperty("mahoutdocumentfrequencypath");

	    Configuration configuration = new Configuration();
	    NaiveBayesModel model = NaiveBayesModel.materialize(new Path(modelPath), configuration);
	    classifier = new StandardNaiveBayesClassifier( model) ;

	    labels = BayesUtils.readLabelIndex(configuration, new Path(labelIndexPath));
	    dictionary = readDictionnary(configuration, new Path(dictionaryPath));
	    documentFrequency = readDocumentFrequency(configuration, new Path(documentFrequencyPath));

	    // analyzer used to extract word from content
	    int labelCount = labels.size();
	    log.info("Docs " + documentFrequency.size());
	    if (documentFrequency.get(-1) != 0) {
		documentCount = documentFrequency.get(-1).intValue();
	    } else {
		documentCount = documentFrequency.get(0).intValue();
	    }
	    log.info("Number of labels: " + labelCount);
	    log.info("Number of documents in training set: " + documentCount);
	} catch (Exception e) {
	    log.error("Exception", e);
	}

    }

    public static String classify(String content) {
	try {
	    Multiset<String> words = ConcurrentHashMultiset.create();
	    // extract words from content
	    int wordCount = getWords(content, dictionary, words);

	    // create vector wordId => weight using tfidf
	    Vector vector = new RandomAccessSparseVector(10000);
	    TFIDF tfidf = new TFIDF();
	    for (Multiset.Entry<String> entry:words.entrySet()) {
		String word = entry.getElement();
		int count = entry.getCount();
		Integer wordId = dictionary.get(word);
		Long freq = documentFrequency.get(wordId);
		double tfIdfValue = tfidf.calculate(count, freq.intValue(), wordCount, documentCount);
		vector.setQuick(wordId, tfIdfValue);
	    }
	    
	    Vector resultVector = classifier.classifyFull(vector);
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
	    log.info(" => " + labels.get(bestCategoryId));
	    return labels.get(bestCategoryId);

	    //Algorithm algorithm = new BayesAlgorithm();
	    //Datastore datastore = new InMemoryBayesDatastore( params );
	    //ClassifierContext classifier =  new ClassifierContext( algorithm, datastore );
	    //classifier.initialize();
	    //ClassifierResult result = classifier.classifyDocument(document.toArray( new String[ document.size() ]));
	} catch (Exception e) {
	    log.error("Exception", e);
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
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );
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
	    log.error("Exception", e);
	}
	return 0;
    }

}

