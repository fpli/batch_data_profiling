package com.ebay.dataquality.profiling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateTest {

  def main(args: Array[String]): Unit = {
    val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    println(yesterday)
  }
}
