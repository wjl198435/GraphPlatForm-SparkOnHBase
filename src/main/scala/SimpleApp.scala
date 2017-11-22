/* SimpleApp.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import scala.math.random
//import org.apache.spark.util.random


object SimpleApp {
//  def main(args: Array[String]) {
//    val logFile = "YOUR_SPARK_HOME/README.md" // Should be some file on your system
//    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
//    val sc = new SparkContext(conf)
//    val logData = sc.textFile(logFile, 2).cache()
//    val numAs = logData.filter(line => line.contains("a")).count()
//    val numBs = logData.filter(line => line.contains("b")).count()
//    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
//  }

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[*]")
    val spark = new SparkContext(conf)
    val slices = if (args.length > 0) args(0).toInt else 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = spark.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x*x + y*y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / (n - 1))
    spark.stop()
  }
}