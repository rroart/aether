package roart.classification;

import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.PipelineModel;

public class SparkMLConfig {
	public Map<String, Map<Double, String>> labelsMap = null;

	public PipelineModel nbm;

	public SparkConf sparkconf;

	public JavaSparkContext jsc;
}
