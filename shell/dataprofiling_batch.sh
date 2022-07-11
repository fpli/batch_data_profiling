#!/bin/bash
# This is for data profiling batch
SPARK_HOME=/apache/spark/spark3.1.1
while true
do

  hh=`date '+%H'`
  echo "current hour: ${hh}"
  if [ ${hh} -eq 07 ]
  then
    echo "do batch at $(date)"
    begin_time=$(date +%s)
    ${SPARK_HOME}/bin/spark-submit --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication  profilingjob-1.0-SNAPSHOT.jar -e "qa" -r "2" > dataprofiling_batch.log 2>&1
    echo "step 1 ----"
    sleep 5
    ${SPARK_HOME}/bin/spark-submit --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication  profilingjob-1.0-SNAPSHOT.jar -e "qa" -r "3" >> dataprofiling_batch.log 2>&1
    echo "step 2 ----"
    sleep 5
    ${SPARK_HOME}/bin/spark-submit --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication  profilingjob-1.0-SNAPSHOT.jar -r "2" >> dataprofiling_batch.log 2>&1
    echo "step 3 ----"
    sleep 5
    ${SPARK_HOME}/bin/spark-submit --master yarn --deploy-mode cluster --class com.ebay.dataquality.application.DataProfilingApplication  profilingjob-1.0-SNAPSHOT.jar -r "3" >> dataprofiling_batch.log 2>&1
    end_time=$(date +%s)
    duration=$(( $end_time - $begin_time ))
    echo "duration = ${duration}"
    sleep 3600
  fi
  echo "job is running at $(date)"
  sleep 600
done