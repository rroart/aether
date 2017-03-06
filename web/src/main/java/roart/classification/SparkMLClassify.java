package roart.classification;

import roart.config.MyConfig;
import roart.lang.LanguageDetect;
import roart.util.Constants;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkMLClassify {

	private static Logger log = LoggerFactory.getLogger(SparkMLClassify.class);

	private static Map<String, Map<Double, String>> labelsMap = null;

	private static PipelineModel nbm;

	private static SparkConf sparkconf;

	private static JavaSparkContext jsc;

	public SparkMLClassify() {
		try {
			labelsMap = new HashMap();
			String[] languages = LanguageDetect.getLanguages();

			// TODO add basepath later
			/*
	    String basepath = MyConfig.conf.sparkbasepath;
	    if (basepath == null) {
	    	basepath = "";
	    }
			 */
			
			String modelPath = MyConfig.conf.sparkmlmodelpath;
			//modelPath = modelPath.replaceAll("LANG", lang);
			String labelIndexPath = MyConfig.conf.sparkmllabelindexpath;
			String sparkmaster = MyConfig.conf.sparkmaster;

			for (String lang : languages) {
				sparkconf = new SparkConf();
				String master = sparkmaster;
				sparkconf.setMaster(master);
				sparkconf.setAppName("aether");
				sparkconf.set("spark.driver.memory", "4g");
				sparkconf.set("spark.executor.memory", "4g");
				// it does not work well with default snappy
				sparkconf.set("spark.io.compression.codec", "lzf");
				//sparkconf.get
				sparkconf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
				sparkconf.set("spark.kryoserializer.buffer.max", "1024m");
				// sparkconf.set("spark.kryo.registrator", "org.apache.mahout.sparkbindings.io.MahoutKryoRegistrator");
				/*
            String userDir = System.getProperty("user.dir");
            log.info("user.dir " + userDir);
            String[] jars = { 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib//guava-16.0.1.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT/WEB-INF/lib/fastutil-7.0.11.jar", 
                    "file:" + userDir + "/target/aether-0.9-SNAPSHOT.jar" 
            };
            System.out.println("here-1");
            sparkconf.setJars(jars);
            //SparkContext sc = new SparkContext(sparkconf);

				 */
				jsc = new JavaSparkContext(sparkconf);
				SQLContext sqlContext = new SQLContext(jsc);
				nbm = PipelineModel.load(modelPath);
				String labelpath = labelIndexPath;
				DataFrame label = sqlContext.read().load(labelpath);
				Map<Double, String> labelMap = new HashMap();
				label.javaRDD().collect().forEach(r-> labelMap.put(r.getAs("id"), r.getAs("cat")));
				labelsMap.put(lang, labelMap);
			}
			System.out.println("Spark ML done");
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		}

	}

	public static String classify(String content, String language) {
		try {
			JavaRDD<Row> jrdd = jsc.parallelize(Arrays.asList(
					RowFactory.create(content)));

			/*
	    StructType schema = new StructType(new StructField[]{
	    		  new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
	    		});*/
			String schemaString = "sentence";

			// Generate the schema based on the string of schema
			List<StructField> fields = new ArrayList<>();
			for (String fieldName : schemaString.split(" ")) {
				StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
				fields.add(field);
			}
			StructType schema = DataTypes.createStructType(fields);

			// TODO check this after spark 2 upgrade
			// https://fossies.org/diffs/spark/1.6.2_vs_2.0.0/examples/src/main/java/org/apache/spark/examples/ml/JavaTfIdfExample.java-diff.html
			SQLContext sqlContext = new SQLContext(jsc);

			DataFrame sentenceDF = sqlContext.createDataFrame(jrdd, schema);
			DataFrame resultDF = nbm.transform(sentenceDF);

			Double predict = resultDF.first().getAs("prediction");
			Map<Double, String> label = labelsMap.get(language);
			String cat = label.get(predict);
			log.info(" cat " + cat);
			return cat;
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		}
		return null;
	}
}

