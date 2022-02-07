package com.ebay.dataquality.profiling.source

import com.ebay.dataquality.DateTimeUtils
import org.apache.spark.sql.DataFrame

case class DateFilter(ago: Int, duration: Int, time_unit: String, date_format: String, filter_clause: String)

trait DateFilterSupport {

  protected var dateFilter: DateFilter = _

  def filterWithDate(df: DataFrame, launchTime: String): DataFrame = {
    if (dateFilter.filter_clause == null || dateFilter.ago <= 0 || dateFilter.duration <= 0
      || dateFilter.date_format == null || dateFilter.time_unit == null) {
      throw new IllegalArgumentException("non-empty 'filter_clause', 'date_format', 'time_unit' and positive 'ago' and 'duration' are required in dataFilter in config")
    }

    if (dateFilter.ago < dateFilter.duration) {
      println("warning: {TO_DATE} is after launch time.")
    }

    val Array(startMs, endMs) = DateTimeUtils.calculateStartEndDatesInMs(launchTime, "yyyyMMdd HH:mm:ss", dateFilter.ago, dateFilter.duration, dateFilter.time_unit)

    val startDate = DateTimeUtils.format(startMs, dateFilter.date_format)
    val endDate = DateTimeUtils.format(endMs, dateFilter.date_format)

    df.filter(dateFilter.filter_clause.replace("${FROM_DATE}", s"'$startDate'").replace("${TO_DATE}", s"'$endDate'"))
  }
}
