#!/bin/bash
# This is for data profiling batch
SPARK_HOME=/apache/hadoop_client/hercules/spark
begin_time=$(date +%)
${SPARK_HOME}/bin/spark-submit --queue hdlq-data-batch-ubi-sle --conf spark.binary.majorVersion=3.1.1 --conf spark.hadoop.majorVersion=3 --files /apache/hadoop_client/hercules/hive/conf/hive-site.xml --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication /dw/etl/home/prod/jar/sg_ubi_data/profilingjob-1.1.jar -e "prod" -r "0" -t "$1"
echo "step 1 ----"
sleep 5
${SPARK_HOME}/bin/spark-submit --queue hdlq-data-batch-ubi-sle --conf spark.binary.majorVersion=3.1.1 --conf spark.hadoop.majorVersion=3 --files /apache/hadoop_client/hercules/hive/conf/hive-site.xml --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication /dw/etl/home/prod/jar/sg_ubi_data/profilingjob-1.1.jar -e "prod" -r "1" -t "$1"
echo "step 2 ----"

end_time=$(date +%s)
duration=$(( $end_time - $begin_time ))
printf "duration = %d\n" $duration