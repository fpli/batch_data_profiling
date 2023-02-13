package com.ebay.dataquality.pojos

import scala.collection.mutable.ArrayBuffer

case class ClassNameAllowedValues(className: String, allowedValues: ArrayBuffer[Any]) extends Serializable
