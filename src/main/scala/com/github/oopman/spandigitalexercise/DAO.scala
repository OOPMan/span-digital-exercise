package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.Constants.Result
import com.github.oopman.spandigitalexercise.Constants.Result.{Result => ResultEnum}
import com.github.oopman.spandigitalexercise.Models.{Results, Teams}
import io.getquill._
import io.getquill.context.jdbc.JdbcContext
import io.getquill.context.sql.idiom.SqlIdiom

import scala.io.Source

/**
  *
  * @param context
  * @param dbInitScript
  * @tparam Dialect
  * @tparam Naming
  */
class DAO[Dialect <: SqlIdiom, Naming <: NamingStrategy](val context: JdbcContext[Dialect, Naming],
                                                         val dbInitScript: Source) {
  import context._

  implicit val encodeResult = MappedEncoding[ResultEnum, Int](_.id)
  implicit val decoderResult = MappedEncoding[Int, ResultEnum](Result(_))

  // Execute DDL statements
  private val statements = dbInitScript.getLines.mkString("\n").split("-- STATEMENT MARKER")
  for (statement <- statements) context.executeAction(statement)

  /**
    * Return all Teams objects
    *
    * @return
    */
  def getTeams: Seq[Teams] = context.run(query[Teams].sortBy(_.id)(Ord.ascNullsLast))

  /**
    * Insert multiple Team objects
    *
    * @param teams An Iterator of team names
    * @return
    */
  def addTeams(teams: Iterable[String]): Seq[Long] = {
    context.run(
      liftQuery(teams).foreach(name => query[Teams].insert(_.name -> name))
    )
  }

  /**
    * Retrieve all Results objects
    *
    * @return
    */
  def getResults: Seq[Results] = {
    context.run(query[Results].sortBy(_.id)(Ord.ascNullsLast))
  }

  /**
    * Insert multiple Result objects
    *
    * @param results An Iterator over results tuples
    * @return
    */
  def addResults(results: Iterable[(Int, ResultEnum, Int)]): Seq[Long] = {
    context.run(
      liftQuery(results)
        .foreach(tuple => query[Results].insert(
          _.teamId -> tuple._1,
          _.result -> tuple._2,
          _.score -> tuple._3
        ))
    )
  }

  /**
    * Gather round children, let's see how well Quill works for queries a little
    * bit more complex than the most basic CRUD operations.
    *
    * For reference, the simplest SQL version of this query (that works in H2)
    * is as follows:
    *
    * <pre>
    * SELECT
    *   T.NAME,
    *   SUM(I.POINTS)
    * FROM TEAMS AS T
    *   INNER JOIN (
    *                SELECT
    *                  TEAM_ID AS TEAM_ID,
    *                  CASE RESULT
    *                  WHEN 0
    *                    THEN 3
    *                  WHEN 1
    *                    THEN 0
    *                  ELSE 1
    *                  END     AS POINTS
    *                FROM RESULTS
    *                GROUP BY TEAM_ID, RESULT
    *              ) AS I ON T.ID = I.TEAM_ID
    * GROUP BY T.ID, T.NAME;
    * </pre>
    *
    * Because of the way Quill works (It's a FRM, not an ORM) building this
    * query actually involves a bit more legwork than one would suspect...
    *
    * @return A sequence of 2-tuples pairing team name with league points
    */
  def calculateLeagueResults: Seq[(String, Int)] = {
    case class TeamIdPoints(teamId: Int, points: Int)
    case class TeamIdTotalPoints(teamId: Int, totalPoints: Option[Int])
    /**
      * Step 1: Build a query which will convert the Win/Loss/Draw results we're
      * storing into league point values
      */
    val teamIdPointsQuery = quote {
      query[Results]
        .map(result => TeamIdPoints(
          result.teamId,
          if (result.result == lift(Result.Win)) 3
          else if (result.result == lift(Result.Loss)) 0
          else 1
        ))
    }
    /**
      * Step 2: Things start to get hairy. We need to sum the league point
      * values for each team. One might be inclined to think that querying
      * against Teams is a bad idea here because Results also has a Team ID
      * (as used in the ON generation) and thus causes an unnecessary JOIN
      * (which is does. Kind of...) but in reality switching this query to
      * use Results seems to break the SQL generation causing Quill to produce
      * a query that runs but which unintentionally doubles up all point values!
      */
    val teamIdTotalPointsQuery = quote {
      query[Teams]
        .join(teamIdPointsQuery)
        .on {
          case (teams, resultPoints) => teams.id == resultPoints.teamId
        }
        .groupBy {
          case (teams, resultPoints) => teams.id
        }
        .map {
          case (teamId, resultPointsQuery) => TeamIdTotalPoints(
            teamId,
            resultPointsQuery.map {
              case (teams, resultPoints) => resultPoints.points
            }.sum
          )
        }
    }

    /**
      * Step 3: The final step is a fair bit simpler, all we need to do here is
      * output the team name paired with its sum league points value. Initially
      * I had attempted to do this using explicit JOIN syntax as used above but
      * ran into a bug with Quill's macros that prevented this from working.
      *
      * Hopefully this will be fixed soon...
      *
      * See:
      * - https://github.com/getquill/quill/issues/1109
      * - https://scastie.scala-lang.org/EmYBkL6FRbyiUPcbSFOr8g
      * - https://scastie.scala-lang.org/mentegy/p2lpqYJlRsCV68PqxhXAiQ
      * - https://gitter.im/getquill/quill chat logs from May 30, 2018
      */
    val teamNameTotalPointsQuery = quote {
      for {
        team <- query[Teams]
        teamIdTotalPoints <- teamIdTotalPointsQuery
        if team.id == teamIdTotalPoints.teamId
      } yield (team.name, teamIdTotalPoints.totalPoints.getOrElse(0))
    }

    /**
      * For reference, the final query produced by Quill looks something like:
      *
      * SELECT
      *   x06result._1,
      *   x06result._2
      * FROM (
      *   SELECT
      *     x06result.total_points _2,
      *     team.name _1
      *   FROM teams team, (
      *     SELECT
      *       x06.id team_id,
      *       SUM(
      *         CASE
      *         WHEN result.result = 0 THEN 3
      *         WHEN result.result = 1 THEN 0
      *         ELSE 1
      *         END
      *       ) total_points
      *     FROM teams x06
      *     INNER JOIN results result ON x06.id = result.team_id
      *     GROUP BY x06.id) x06result
      *     WHERE team.id = x06result.team_id
      * ) x06result
      * ORDER BY x06result._2 DESC NULLS LAST
      *
      * It's quite a bit more verbose than the handwritten version and includes
      * an extra join that isn't really necessary but I suppose it could be
      * worse :-)
      */
    context.run(teamNameTotalPointsQuery.sortBy(_._2)(Ord.descNullsLast))
  }
}
