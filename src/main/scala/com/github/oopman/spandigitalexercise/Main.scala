package com.github.oopman.spandigitalexercise

import java.io.File
import scopt._


case class Config(dbUri: String=Constants.defaultDbUriSuffix,
                  inputs: Seq[File]=Nil)

/**
  * Entry point to SPAN Digital Exercise
  *
  * Implements a CLI that allows for input of multiple source data files
  * along with tweaking of some options
  */
object Main extends App {
  val parser = new OptionParser[Config]("SPAN Digital Exercise") {
    head("SPAN Digital Exercise", "0.1")

    opt[String]('d', "db-uri-suffix")
      .action( (value: String, config: Config) => {
        config.copy(dbUri=value)
      })
      .text(
        s"""JDBC database URI suffix to use for data source. Defaults to ${Constants.defaultDbUriSuffix}.
           |Affixed to ${Constants.dbUriPrefix} to produce complete JDBC URI""".stripMargin)

    opt[File]("<file>...")
      .unbounded()
      .optional()
      .action( (value: File, config: Config) => {
        config.copy(inputs=config.inputs :+ value)
      })
      .text("Input files")
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      // TODO: Begin processing
    case None =>
      // TODO: Error handling
  }

}
