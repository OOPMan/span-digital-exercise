package com.github.oopman.spandigitalexercise

import java.io.File
import scopt._


case class Config(dbUri: String=Constants.defaultDbURI,
                  inputs: Seq[File]=Nil)

object Main extends App {
  val parser = new OptionParser[Config]("SPAN Digital Exercise") {
    head("SPAN Digital Exercise", "0.1")

    opt[String]('d', "dburi")
      .action( (value: String, config: Config) => {
        config.copy(dbUri=value)
      })
      .text(s"JDBC database URI to use for data source. Defaults to ${Constants.defaultDbURI}")

    opt[File]("<file>...")
      .unbounded()
      .optional()
      .action( (value: File, config: Config) => {
        config.copy(inputs=config.inputs :+ value)
      })
      .text("Input files")
  }

}
