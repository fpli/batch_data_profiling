package com.ebay.dataquality.common

import com.ebay.dataquality.util.EnvUtil

trait TDao {

  def readFile(path: String) = {
    EnvUtil.take().sparkContext.textFile(path)
  }

  def executeSparkSQL(sparkSQL: String) = {
    EnvUtil.take().sql(sparkSQL)
  }

}
