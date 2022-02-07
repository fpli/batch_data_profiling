package com.ebay.dataquality.profiling

import scopt.OParser

trait JobArgumentsEnabled {

  protected var jobArguments: JobArguments = JobArguments()

  val builder = OParser.builder[JobArguments]

  val parser1 = {
    import builder._
    OParser.sequence(
      programName("ProfilingJob"),
      head("ProfilingJob", "0.0.1"),
      // option -f, --foo
      opt[String]('c', "config_file_location")
        .required()
        .action((x, c) => c.copy(configFileLocation = x))
        .text("config file location"),

      opt[Unit]('l', "local")
        .optional()
        .action((_, c) => c.copy(isLocal = true))
        .text("is local env?"),
      opt[String]('n', "name")
        .optional()
        .action((n, c) => c.copy(appName = n))
        .text("application name"),
      opt[String]('t', "launch-time")
        .required()
        .action((n, c) => c.copy(launchTime = n))
        .text("reference time when the job starts"),
      opt[String]('j', "job-log")
        .optional()
        .action((n, c) => c.copy(job_log_url = n))
        .text("url for logging jobs")
    )
  }

  def parse(args: Array[String]) = {
    OParser.parse(parser1, args, JobArguments()) match {
      case Some(ja) => Some(ja)
      case _ => {
        OParser.usage(parser1)
        None
        //throw new IllegalArgumentException("invalid arguments")
      }
    }
  }
}

case class JobArguments(configFileLocation: String = "", appName: String = "ProfilingJob", isLocal: Boolean = false, launchTime: String = "", job_log_url: String = "")