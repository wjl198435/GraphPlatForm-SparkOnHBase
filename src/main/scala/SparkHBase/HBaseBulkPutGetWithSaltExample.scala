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
object HBaseBulkPutGetWithSaltExample {
   def main(args: Array[String]) {


     val sparkConf = new SparkConf().setAppName("HBaseBulkPutGetWithSaltExample ")

     sparkConf.setMaster("local[*]")

     val sc = new SparkContext(sparkConf)
     val table="mytableSalt"
     val columnFamily="mycf"

     try {
//       sc.parallelize(1 to 1000)
//         .map(i => (pad(i.toString, 5), "A value"))
//         .toHBaseTable(table)
//         .inColumnFamily(columnFamily)
//         .toColumns("col")
//         .withSalting((0 to 9).map(s => s.toString))
//         .save()

       val rdd = sc.hbaseTable[String](table)
         .select("col")
         .inColumnFamily(columnFamily)
         .withStartRow("00501")
         .withSalting((0 to 9).map(s => s.toString))

       rdd.foreach(f=>println(f))


     } finally {
       sc.stop()
     }
   }

  def pad(str: String, size: Int): String =
    if(str.length>=size) str
    else pad("0" + str, size)
 }

