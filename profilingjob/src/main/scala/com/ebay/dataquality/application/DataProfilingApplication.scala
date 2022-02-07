package com.ebay.dataquality.application

import com.ebay.dataquality.common.TApplication
import com.ebay.dataquality.controller.DataProfilingController
import com.ebay.dataquality.profiling.Loggable
import scopt.{OParser, OParserBuilder}

object DataProfilingApplication extends TApplication with Loggable{

  private val builder: OParserBuilder[Parameters] = OParser.builder[Parameters]

  val parser = {
    import builder._
    OParser.sequence(
      programName("DataProfilingApplication"),

      opt[Unit]('l', "local")
        .optional()
        .action((_, c) => c.copy(isLocal = true))
        .text("is local env?"),

      opt[String]('t', "date")
        .optional()
        .action((n, c) => c.copy(date = n))
        .text("reference time when the job starts"),

      opt[String]('e', "env")
        .optional()
        .action((n, c) => c.copy(env = n))
        .text("reference time when the job starts")
    )
  }

  def parse(args: Array[String]) = {
    OParser.parse(parser, args, Parameters()) match {
      case Some(ja) => Some(ja)
      case _ => Some(Parameters())
    }
  }

  def main(args: Array[String]): Unit = {

    val maybeParameters = parse(args)

    start("yarn", "DataProfilingApplication"){
      val dataProfilingController = new DataProfilingController
      dataProfilingController.dispatch(maybeParameters)
    }
  }

}

case class Parameters(isLocal: Boolean = false, date: String = "", env: String = "prod")
