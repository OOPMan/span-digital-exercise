package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.CLI.getContext
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.scalatest.WordSpec


class DAOSpec extends WordSpec {
  val dataSourceConfig = new HikariConfig()
  dataSourceConfig.setJdbcUrl(Constants.defaultDbUri)
  dataSourceConfig.setDriverClassName("org.h2.Driver")
  val dataSource = new HikariDataSource(dataSourceConfig)
  val dao = new DAO(
    getContext(Constants.defaultDbUri, dataSource),
    CLI.getDbInitScript(Constants.defaultDbUri)
  )

  "A DAO" can {
    "Add Teams" in {
      assert(dao.addTeam("1st Team") == 1)
      assert(dao.addTeam("2nd Team") == 2)
    }

    "Retrieve/Create a Team by Name" in {
      assert(dao.getTeam("1st Team").id == 1)
      assert(dao.getTeam("3rd Team").id == 3)
    }

    "Retrieve all Teams" in {
      assert(dao.getTeams.length == 3)
    }

    "Add Results" in {
      assert(dao.addResult(1, Constants.Result.Win, 3) == 1)
      assert(dao.addResult(1, Constants.Result.Draw, 1) == 2)
      assert(dao.addResult(2, Constants.Result.Loss, 0) == 3)
      assert(dao.addResult(2, Constants.Result.Draw, 1) == 4)
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
