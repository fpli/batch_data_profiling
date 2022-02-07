package com.ebay.dataquality.profiling.source

trait HasSQL {

  def getSQL(): String

  def getTableName(): String

}
