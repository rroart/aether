import java.io.File

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.types.StructField
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.feature.{HashingTF, IDF, Normalizer}
import org.apache.spark.ml.feature.{Tokenizer}
import org.apache.spark.ml.{Pipeline}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._

val traindir = "/home/roart/usr/local/mycrawler/sparkmy/train"
val testdir = "/home/roart/usr/local/mycrawler/sparkmy/test"

def createLabelMap(path: String): Map[String, Double] = {
val f = new File(path)
val l = f.list
l.map(x => x -> l.indexOf(x).toDouble).toMap
}

val schema = StructType(
StructField("id", StringType) ::
StructField("label", DoubleType) :: 
StructField("sentence", StringType) :: Nil)

val labelToNumeric = createLabelMap(traindir)
val labelDF = spark.createDataFrame(labelToNumeric.toSeq).toDF("cat", "id")
labelDF.write.mode(SaveMode.Overwrite).save("my.label")

val trainfiles = sc.wholeTextFiles(traindir + "/*").map(rawText => Row(rawText._1.split("/").last, labelToNumeric(rawText._1.split("/").init.last), rawText._2))

val traindata = spark.createDataFrame(trainfiles, schema)

val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures")
val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
val nb = new NaiveBayes()
val pipeline = new Pipeline().setStages(Array(tokenizer, hashingTF , idf, nb))
println("Tokenizer:")
println(tokenizer.explainParams())
println("*************************")
println("HashingTF:")
println(hashingTF.explainParams())
println("*************************")
println("IDF:")
println(idf.explainParams())
println("*************************")
println("NaiveBayes:")
println(nb.explainParams())
println("*************************")
println("Pipeline:")
println(pipeline.explainParams())

val frames = traindata.randomSplit(Array(0.2, 0.8))
val testdata = frames(0)

val model = pipeline.fit(traindata)

val runtime = Runtime.getRuntime
println("sys " + System.getProperty("sun.arch.data.model") + " " + runtime.totalMemory + " " + runtime.freeMemory + " " + runtime.maxMemory)

println("save model")

model.write.overwrite.save("my.model")

val predictiontrain = model.transform(traindata)
val predictiontest = model.transform(testdata)

val evaluator = new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("accuracy")
val accuracytrain = evaluator.evaluate(predictiontrain)
val accuracytest = evaluator.evaluate(predictiontest)
println("Accuracy " + accuracytrain + " : " + accuracytest)
