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
import org.apache.spark.{SparkConf, SparkContext}

/**
 * This is a simple example of putting records in HBase
 * with the bulkPut function.
 */
object HBaseBulkPutExample {
   def main(args: Array[String]) {
     if (args.length < 2) {
       println("HBaseBulkPutExample {tableName} {columnFamily}")
       return
     }

     val tableName = args(0)
     val columnFamily = args(1)

     val sparkConf = new SparkConf().setAppName("HBaseBulkPutExample " +
       tableName + " " + columnFamily)

     sparkConf.setMaster("local[*]")

     val sc = new SparkContext(sparkConf)

     try {
       val rdd = sc.parallelize(1 to 10000000)
         .map(i => (i.toString, i+1, "Hello"))

       rdd.toHBaseTable("mytable1")
         .toColumns("column1", "column2")
         .inColumnFamily("mycf")
         .save()

     } finally {
       sc.stop()
     }
   }
 }

