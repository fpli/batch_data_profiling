package com.ebay.dataquality.common

import com.ebay.dataquality.application.Parameters

trait TController {

  def dispatch(option: Option[Parameters]): Unit

}
