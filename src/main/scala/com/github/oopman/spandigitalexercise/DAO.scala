package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.Constants.Result
import com.github.oopman.spandigitalexercise.Constants.Result.{Result => ResultEnum}
import com.github.oopman.spandigitalexercise.Models.{Results, Teams}
import io.getquill.{H2JdbcContext, NamingStrategy}

class DAO[N <: NamingStrategy](val context: H2JdbcContext[N]) {
  import context._

  implicit val encodeResult = MappedEncoding[ResultEnum, Int](_.id)
  implicit val decoderResult = MappedEncoding[Int, ResultEnum](Result(_))

  /**
    * Return all Teams objects
    *
    * @return
    */
  def getTeams: Seq[Teams] = {
    context.run(query[Teams].sortBy(_.id)(Ord.ascNullsLast))
  }

  /**
    * Insert a Team object
    *
    * @param name Name of Team
    * @return
    */
  def addTeam(name: String): Long = {
    context.run(query[Teams].insert(_.name -> lift(name)))
  }

  /**
    * Retrieve all Results objects
    *
    * @return
    */
  def getResults: Seq[Results] = ???

  /**
    * Insert a Result object
    *
    * @param teamId PK of Team associated with Result
    * @param result Result enumeration value
    * @return
    */
  def addResult(teamId: Int, result: ResultEnum): Long = {
    context.run(
      query[Results]
        .insert(
          _.teamId -> lift(teamId),
          _.result -> lift(result)
        )
    )
  }
}