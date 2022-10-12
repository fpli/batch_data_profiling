package com.ebay.dataquality.util

import java.util.regex.Pattern

object SpecialTagCheck {

  def orderCntCheck(orderCnt: String): Boolean = {
    if (orderCnt == null) {
      false
    } else {
      Pattern.compile("^\\d+$").matcher(orderCnt).matches()
    }
  }


  def isTableOrNot(ist: String): Boolean = {
    if (ist == null) {
      false
    } else {
      try {
        val i = ist.toInt
        if (i == 0 || i == 1) true else false
      } catch {
        case e: Exception => false
      }
    }
  }

  def paginationCheck(pagination: String): Boolean = {
    if (pagination == null) {
      false
    } else {
      Pattern.compile("^\\d+$").matcher(pagination).matches()
    }
  }


  def sojReplaceChar(origin: String, charList: String, replaceChar: String): String = {
    if (origin == null || charList == null || replaceChar == null) {
      null
    } else if (origin == "" || charList == "") {
      ""
    } else {
      val firstChar = if (replaceChar.nonEmpty) replaceChar.substring(0, 1) else replaceChar
      origin.replaceAll(s"[${charList}]", firstChar)
    }
  }

  def checkGuid(guid: String): Boolean = {
    if (sojReplaceChar(guid, "0123456789abcdefABCDEF", "") != "") {
      false
    } else
      true
  }

  def mobileVersionCheck(mobileVersion: String): Boolean = {
    if (sojReplaceChar(mobileVersion, "01234567890.", "") != "") {
      false
    } else
      true
  }

  def checkSiteId(siteId: String, option: Option[List[Any]]): Boolean = {
    val notAllowedSiteIds = option.get
    if (notAllowedSiteIds == null) {
      return true
    }
    try {
      val decimal = BigDecimal(siteId)
      !notAllowedSiteIds.contains(decimal)
    } catch {
      case e: Exception => true
    }
  }

}
