package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.CLI.getContext
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.scalatest.WordSpec


class IngesterSpec extends WordSpec {
  val dataSourceConfig = new HikariConfig()
  dataSourceConfig.setJdbcUrl(Constants.defaultDbUri)
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

  }
}
