package SparkHBase

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

////package org.apache.hadoop.hbase.spark.example.rdd

import it.nerdammer.spark.hbase._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.SparkSession

/**
  * This is a simple example of putting records in HBase
  * with the bulkPut function.
  */

case class Person(name: String, age: Int,desc:String)
object HBaseBulkGetExample {
  def main(args: Array[String]) {


    val sparkConf = new SparkConf().setAppName("HBaseBulkGetExampleJob ")

    sparkConf.setMaster("local[*]")

    val HBaseTable="mytable1"
    val ColumFamily="mycf"
    val Column1="column1"
    val Column2="column2"



    val sc = new SparkContext(sparkConf)

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    try {

      //Read From Hbase
      val hBaseRDD = sc.hbaseTable[(String, Int, String)](HBaseTable)
        .select(Column1, Column2)
        .inColumnFamily(ColumFamily)

      hBaseRDD.take(10).foreach(f=>println(f._1,f._2,f._3))

      //Read with Row Filter
      val rddRowFilter = sc.hbaseTable[(String, Int)](HBaseTable)
        .select(Column1)
        .inColumnFamily(ColumFamily)
        .withStartRow("100000")
        .withStopRow("100500")

      rddRowFilter.take(10).foreach(f=>println(f._1,f._2))

     //Managing Empty Columns
      val rddEmptyCol = sc.hbaseTable[(Option[String], Int, String)](HBaseTable)
        .select(Column1,Column2)
        .inColumnFamily(ColumFamily)

      rddEmptyCol.take(10).foreach(t => {
        if(t._1.nonEmpty) println(t._1.get)
      })

      //HBaseRDD as Spark Dataframe


      val hBaseRDD_DF = sc.hbaseTable[( Option[String],Option[Int], Option[String])](HBaseTable).select(Column1, Column2).inColumnFamily(ColumFamily)

      //method1
      val rowRDD = hBaseRDD_DF.map(i => Person(i._1.get,i._2.get,i._3.get)).toDF()
          rowRDD.show(10)

      //method2
      val rowRDD1 = hBaseRDD_DF.map(i => Row(i._1.get,i._2.get,i._3.get))
      object myschema {
        val column1 = StructField("column1", StringType)
        val column2 = StructField("column2", IntegerType)
        val column3 = StructField("column3", StringType)
        val struct = StructType(Array(column1,column2,column3))
      }

      val myDf = sqlContext.createDataFrame(rowRDD1,myschema.struct)
      myDf.show(10)

      //SparkSQL on HBase

      myDf.registerTempTable("mytable")
      sqlContext.sql("SELECT * FROM mytable").show(20)


    } finally {
      sc.stop()
    }
  }
}

