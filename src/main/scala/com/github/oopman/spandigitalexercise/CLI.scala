package com.github.oopman.spandigitalexercise

import java.io.File

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.context.jdbc.JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill._
import scopt.OptionParser

import scala.io.Source

object CLI {
  case class Config(dbUri: String=Constants.defaultDbUri,
                    dbInitScript: Option[File]=None,
                    inputs: Seq[File]=Nil)

  /**
    * Generates a Quill Context based on the input dbUri using the provided
    * DataSource
    *
    * @param dbUri JDBC database URI
    * @param dataSource Hikari DataSource
    * @return
    */
  def getContext(dbUri: String, dataSource: HikariDataSource): JdbcContext[_ <: SqlIdiom, _ <: NamingStrategy] = {
    dbUri match {
      case dbUri if dbUri.startsWith("jdbc:h2") => new H2JdbcContext(SnakeCase, dataSource)
      case dbUri if dbUri.startsWith("jdbc:postgresql") => new PostgresJdbcContext(SnakeCase, dataSource)
      case dbUri if dbUri.startsWith("jdbc:mysql") => new MysqlJdbcContext(SnakeCase, dataSource)
      case dbUri if dbUri.startsWith("jdbc:sqlite") => new SqliteJdbcContext(SnakeCase, dataSource)
      case dbUri if dbUri.startsWith("jdbc:sqlserver") => new SqlServerJdbcContext(SnakeCase, dataSource)
      case _ => throw new RuntimeException(s"Unsupported database URI $dbUri. Only H2, PostgreSQL, MySQL, SQLite and MS SQL Server are supported")
    }
  }

  /**
    * Retrieves an appropraite DB init script based on the input dbUri
    *
    * @param dbUri JDBC database URI
    * @return
    */
  def getDbInitScript(dbUri: String): Source = {
    dbUri match {
      case dbUri if dbUri.startsWith("jdbc:h2") => Source.fromResource("database.h2.sql")
      case dbUri if dbUri.startsWith("jdbc:postgresql") => Source.fromResource("database.postgresql.sql")
      case dbUri if dbUri.startsWith("jdbc:mysql") => Source.fromResource("database.mysql.sql")
      case dbUri if dbUri.startsWith("jdbc:sqlite") => Source.fromResource("database.sqlite.sql")
      case dbUri if dbUri.startsWith("jdbc:sqlserver") => Source.fromResource("database.sqlserver.sql")
      case _ => throw new RuntimeException(s"Unsupported database URI $dbUri. Only H2, PostgreSQL, MySQL, SQLite and MS SQL Server are supported")
    }
  }

  private val parser = new OptionParser[Config]("SPAN Digital Exercise") {
    head("SPAN Digital Exercise", "0.1")

    opt[String]('d', "db-uri")
      .action( (value: String, config: Config) => {
        config.copy(dbUri=value)
      })
      .text(s"JDBC database URI to use for data source. Defaults to ${Constants.defaultDbUri}")

    opt[File]('i', "db-init-script")
      .action( (value: File, config: Config) => {
        config.copy(dbInitScript = Some(value))
      })
      .text("JDBC database init script. Required for if using a JDBC database that is not H2")

    arg[File]("<file>...")
      .unbounded()
      .optional()
      .action( (value: File, config: Config) => {
        config.copy(inputs=config.inputs :+ value)
      })
      .text("Input match data files")
  }

  /**
    *
    * @param args CLI arguments to parse
    * @return
    */
  def apply(args: Seq[String]): Int = parser.parse(args, Config()) match {
    case Some(config) =>
      val dataSourceConfig = new HikariConfig()
      dataSourceConfig.setJdbcUrl(config.dbUri)
      val dataSource = new HikariDataSource(dataSourceConfig)
      val context = getContext(config.dbUri, dataSource)
      val dbInitScript = getDbInitScript(config.dbUri)
      val dao = new DAO(context, dbInitScript)
      val ingester = new Ingester(dao)
      val resultsIngested = ingester.ingestSources(config.inputs.map(Source.fromFile))
      val leagueResults = dao.calculateLeagueResults
      for ((teamName, leaguePoints) <- leagueResults) {
        println(s"$teamName, $leaguePoints pts".trim)
      }
      0
    case None =>
      // Error
      1
  }
}
