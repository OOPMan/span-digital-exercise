package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.CLI.getContext
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.NamingStrategy
import io.getquill.context.sql.idiom.SqlIdiom
import org.scalatest.{BeforeAndAfter, WordSpec}

class DAOSpec extends WordSpec with BeforeAndAfter {
  var dataSourceConfig: HikariConfig = _
  var dataSource: HikariDataSource = _
  var dao: DAO[_ <: SqlIdiom, _ <: NamingStrategy] = _

  before {
    dataSourceConfig = new HikariConfig()
    dataSourceConfig.setJdbcUrl(Constants.defaultDbUri)
    dataSource = new HikariDataSource(dataSourceConfig)
    dao = new DAO(
      getContext(Constants.defaultDbUri, dataSource),
      CLI.getDbInitScript(Constants.defaultDbUri)
    )
  }

  "A DAO" can {
    "Add Teams" in {
      assert(dao.addTeam("1st Team") == 1L)
      assert(dao.addTeam("2nd Team") == 2L)
    }
  }

}
