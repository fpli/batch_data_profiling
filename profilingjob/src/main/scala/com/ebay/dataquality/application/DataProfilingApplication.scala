package com.ebay.dataquality.application

import com.ebay.dataquality.common.TApplication
import com.ebay.dataquality.controller.DataProfilingController
import com.ebay.dataquality.util.EnvUtil
import scopt.{OParser, OParserBuilder}

object DataProfilingApplication extends TApplication {

  private val builder: OParserBuilder[Parameters] = OParser.builder[Parameters]

  private val parser = {
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
        .text("which day to run, e.g: '20220205000000'"),

      opt[String]('e', "env")
        .optional()
        .action((n, c) => c.copy(env = n))
        .text("which env to use: qa or prod"),

      opt[String]('r', "request")
        .optional()
        .action((n, c) => c.copy(request = n))
        .text("which request to perform")
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
    if (maybeParameters.isEmpty) {
      println("parse error: please check the input parameters: " + args.mkString(","))
      System.exit(-1)
    }
    start("yarn", EnvUtil.getRequest(maybeParameters.get.request).getOrElse("DataProfilingApplication")){
      val dataProfilingController = new DataProfilingController
      dataProfilingController.dispatch(maybeParameters)
    }
  }

}

case class Parameters(isLocal: Boolean = false, date: String = "", env: String = "prod", request: String = "-1")
