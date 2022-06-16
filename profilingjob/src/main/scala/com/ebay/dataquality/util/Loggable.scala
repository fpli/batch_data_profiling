package com.ebay.dataquality.util

import org.slf4j.{Logger, LoggerFactory}

trait Loggable {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}
