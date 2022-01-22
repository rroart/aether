package roart.classification.spark.ml;

import roart.classification.MachineLearningAbstractClassifier;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.common.machinelearning.MachineLearningClassifyResult;
import roart.common.machinelearning.MachineLearningConstructorResult;
import roart.common.util.IOUtil;

import java.util.Map;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.execution.datasources.DataSource;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkMLClassify extends MachineLearningAbstractClassifier {

	private static Logger log = LoggerFactory.getLogger(SparkMLClassify.class);

	private SparkMLConfig conf;

	public SparkMLClassify(String nodename, NodeConfig nodeConf) {
            super(nodename, nodeConf);
		try {
			conf = new SparkMLConfig();

			conf.labelsMap = new HashMap<>();

			String basepath = nodeConf.getSparkMLBasePath();
			if (basepath == null) {
				basepath = "";
			}

			String modelPath = basepath + nodeConf.getSparkMLModelPath();
			String labelIndexPath = basepath + nodeConf.getSparkMLLabelIndexPath();

			SparkConf sparkconf = new SparkConf();
			String sparkmaster = nodeConf.getSparkMLSparkMaster();
			log.info("sparkmaster " + sparkmaster);
			String master = sparkmaster;
			sparkconf.setMaster(master);
			sparkconf.setAppName("aether");
			sparkconf.set("spark.driver.memory", "4g");
			sparkconf.set("spark.executor.memory", "4g");
			// it does not work well with default snappy
			sparkconf.set("spark.io.compression.codec", "lzf");
			//sparkconf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
			//sparkconf.set("spark.kryoserializer.buffer.max", "1024m");

			SparkSession spark = SparkSession
					.builder()
					.master(sparkmaster)
					.appName("Aether")
					.config(sparkconf)
					.getOrCreate();

			conf.spark = spark;

			String[] languages = nodeConf.getLanguages();
			log.info("L {} {}", languages.length, modelPath);
			var source = "parquet";
			var cls = DataSource.lookupDataSource(source, spark.sessionState().conf());
			log.info("C {}", cls);
			for (String lang : languages) {
				conf.nbm = PipelineModel.load(modelPath.replaceAll("LANG", lang));
				Dataset<Row> label = spark.read().load(labelIndexPath.replaceAll("LANG", lang));
				Map<Double, String> labelMap = new HashMap<>();
				label.javaRDD().collect().forEach(r-> labelMap.put(r.getAs("id"), r.getAs("cat")));
				conf.labelsMap.put(lang, labelMap);
			}
			log.info("Spark ML done");
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		}

	}

	public MachineLearningConstructorResult destroy(String nodename) {
		conf.spark.stop();
		return null;
	}

	public MachineLearningClassifyResult classify(MachineLearningClassifyParam classify) {
            Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            InputStream validateStream = inmemory.getInputStream(classify.message);
            if (!InmemoryUtil.validate(classify.message.getMd5(), validateStream)) {
                MachineLearningClassifyResult result = new MachineLearningClassifyResult();
                return result;
            }
            String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArrayMax(inmemory.getInputStream(classify.message)));
		String language = classify.language;
		try {
			SparkSession spark = conf.spark;
			List<Row> jrdd = Arrays.asList(
					RowFactory.create(content));

			String schemaString = "sentence";

			// Generate the schema based on the string of schema
			List<StructField> fields = new ArrayList<>();
			for (String fieldName : schemaString.split(" ")) {
				StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
				fields.add(field);
			}
			StructType schema = DataTypes.createStructType(fields);

			Dataset<Row> sentenceDF = spark.createDataFrame(jrdd, schema);
			Dataset<Row> resultDF = conf.nbm.transform(sentenceDF);

			Double predict = resultDF.first().getAs("prediction");
			Map<Double, String> label = conf.labelsMap.get(language);
			String cat = label.get(predict);
			log.info(" cat " + cat);
			MachineLearningClassifyResult result = new MachineLearningClassifyResult();
			result.result = cat;
			return result;
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
		}
		return null;
	}
}

