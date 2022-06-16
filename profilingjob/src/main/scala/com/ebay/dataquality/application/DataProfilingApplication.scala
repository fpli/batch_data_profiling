package com.ebay.dataquality.application

import com.ebay.dataquality.common.TApplication
import com.ebay.dataquality.controller.DataProfilingController
import scopt.{OParser, OParserBuilder}

object DataProfilingApplication extends TApplication {

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
        .text("which day to run, e.g: '20220205'"),

      opt[String]('e', "env")
        .optional()
        .action((n, c) => c.copy(env = n))
        .text("which env to use: qa or prod"),

      opt[String]('r', "request")
        .optional()
        .action((n, c) => c.copy(request = n))
        .text("which request to perforce")
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

case class Parameters(isLocal: Boolean = false, date: String = "", env: String = "prod", request: String = "-1")
