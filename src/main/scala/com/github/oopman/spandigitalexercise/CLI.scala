package com.github.oopman.spandigitalexercise

import java.io.File

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.{H2JdbcContext, SnakeCase}
import scopt.OptionParser

object CLI {
  case class Config(dbUri: String=Constants.defaultDbUriSuffix,
                    inputs: Seq[File]=Nil)

  private val parser = new OptionParser[Config]("SPAN Digital Exercise") {
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

  /**
    *
    * @param args CLI arguments to parse
    * @return
    */
  def apply(args: Seq[String]): Int = parser.parse(args, Config()) match {
    case Some(config) =>
      val dataSourceConfig = new HikariConfig()
      dataSourceConfig.setJdbcUrl(s"${Constants.dbUriPrefix}${config.dbUri}")
      val dataSource = new HikariDataSource(dataSourceConfig)
      val context = new H2JdbcContext(SnakeCase, dataSource)
      0
    case None =>
      // Error
      1
  }
}
