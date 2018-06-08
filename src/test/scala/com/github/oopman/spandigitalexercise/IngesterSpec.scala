package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.CLI.getContext
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.scalatest.WordSpec

import scala.io.Source


class IngesterSpec extends WordSpec {
  val dataSourceConfig = new HikariConfig()
  dataSourceConfig.setJdbcUrl("jdbc:h2:mem:ingesterspec")
  dataSourceConfig.setDriverClassName("org.h2.Driver")
  val dataSource = new HikariDataSource(dataSourceConfig)
  val dao = new DAO(
    getContext(Constants.defaultDbUri, dataSource),
    CLI.getDbInitScript(Constants.defaultDbUri)
  )
  val ingester = new Ingester(dao)
  val lines = List(
    "1st Team 3, 2nd Team 0",
    "1st Team 1, 2nd Team 1"
  )

  "An Ingester" can {
    "Process a match record line" in {
      ingester.processLine(lines.head) match {
        case Array(Some((team1Name, team1Score)), Some((team2Name, team2Score))) =>
          assert(team1Name == "1st Team")
          assert(team1Score == 3)
          assert(team2Name == "2nd Team")
          assert(team2Score == 0)
      }
    }

    "Process a match record array" in {
      ingester.processLineAsArray(ingester.processLine(lines.head)) match {
        case Some(((team1Name, team1Result, team1Score), (team2Name, team2Result, team2Score))) =>
          assert(team1Name == "1st Team")
          assert(team1Result == Constants.Result.Win)
          assert(team1Score == 3)
          assert(team2Name == "2nd Team")
          assert(team2Result == Constants.Result.Loss)
          assert(team2Score == 0)
      }
    }

    "Process a scala.io.Source instance" in {
      assert(ingester.ingestSource(Source.fromString(lines.mkString("\n"))))
    }

    "Process multiple scala.io.Source instance" in {
      val sources = List(Source.fromString(lines.mkString("\n")),
                         Source.fromString(lines.mkString("\n")))
      assert(ingester.ingestSources(sources) > 0)
    }
  }
}
