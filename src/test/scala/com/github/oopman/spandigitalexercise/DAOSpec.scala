package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.CLI.getContext
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.scalatest.WordSpec


class DAOSpec extends WordSpec {
  val dataSourceConfig = new HikariConfig()
  dataSourceConfig.setJdbcUrl("jdbc:h2:mem:daospec")
  dataSourceConfig.setDriverClassName("org.h2.Driver")
  val dataSource = new HikariDataSource(dataSourceConfig)
  val dao = new DAO(
    getContext(Constants.defaultDbUri, dataSource),
    CLI.getDbInitScript(Constants.defaultDbUri)
  )

  "A DAO" can {
    "Add Teams" in {
      assert(dao.addTeams("1st Team" :: "2nd Team" :: Nil).nonEmpty)
    }

    "Retrieve all Teams" in {
      assert(dao.getTeams.length == 2)
    }

    "Add Results" in {
      val results = List(
        (1, Constants.Result.Win, 3),
        (1, Constants.Result.Draw, 1),
        (2, Constants.Result.Loss, 0),
        (2, Constants.Result.Draw, 1)
      )
      assert(dao.addResults(results).nonEmpty)
    }

    "Retrieve all Results" in {
      assert(dao.getResults.length == 4)
    }

    "Calculate League Results" in {
      val results = dao.calculateLeagueResults
      assert(results.head == ("1st Team", 4))
      assert(results(1) == ("2nd Team", 1))
      assertThrows[IndexOutOfBoundsException](results(2))
    }
  }
}
