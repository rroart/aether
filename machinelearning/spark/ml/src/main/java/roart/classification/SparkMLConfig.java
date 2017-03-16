package roart.classification;

import java.util.Map;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.SparkSession;

public class SparkMLConfig {
	public Map<String, Map<Double, String>> labelsMap = null;

	public PipelineModel nbm;

	public SparkSession spark;
}
