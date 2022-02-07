package com.ebay.dataquality.profiling

import com.ebay.dataquality.config.ExpressionConfig
import org.apache.spark.sql.functions._

object TransformExpression {
  def toColumn(cfg: ExpressionConfig) = {
    cfg.getOperator match {
      case "SQL" => {
        val text = cfg.getConfig.get("text")
        assert(text != null, "text is mandatory for SQL expression")
        val strText = text.asInstanceOf[String]
        expr(strText)
      }
      case _ => throw new IllegalArgumentException("unknown transform expression operator: " + cfg.getOperator)
    }
  }
}
