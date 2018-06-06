package com.github.oopman.spandigitalexercise

import scala.io.Source
import io.getquill.NamingStrategy
import com.github.oopman.spandigitalexercise.Constants.Result
import com.github.oopman.spandigitalexercise.Constants.Result.Result
import io.getquill.context.sql.idiom.SqlIdiom

import scala.util.matching.Regex

/**
  * This class encapsulates the logic involved in ingesting input data for Teams
  * and Results
  *
  * @param dao Instance of DAO
  * @tparam Dialect SqlIdiom upper-bounded type parameter
  * @tparam Naming NamingStrategy upper-bounded type parameter
  */
class Ingester[Dialect <: SqlIdiom, Naming <: NamingStrategy](dao: DAO[Dialect, Naming]) {
  val resultPattern: Regex = raw"(.+) (\d+)".r
  /**
    * Ingest data from a Sequence of File instances, returning the count of
    * Files successfully ingested
    *
    * @param sources A Sequence of Files
    * @return Number of files successfully ingested
    */
  def ingestSources(sources: Seq[Source]): Int = sources.map(ingestSource).count(_ == true)

  /**
    * Ingest data from a single File instance, returning true in the instance of
    * a successful ingestion
    *
    * @param source A File to ingest
    * @return
    */
  def ingestSource(source: Source): Boolean = {
    dao.addResults(
      source.getLines
      .map(processLine)
      .map(processLineAsArray)
      .filter(_.isDefined)
      .flatMap(_.get)
    ).sum > 0
  }

  /**
    * Convert a line into an Array of Optional 2-tuples containing the Team Name
    * and Score
    *
    * @param line A line of text
    * @return
    */
  def processLine(line: String): Array[Option[(String, Int)]] = {
    line.trim.split(',').map {
      case resultPattern(teamName, score) => Some((teamName.trim, score.toInt))
      case _ => None
    }
  }

  /**
    * Convert a value produced by processLine into an Optional List of 3-tuples
    * containing Team ID, Result value and Team Score. The data in these 3-tuples
    * is suitable for submission to DAO.addResult
    *
    * @param lineAsArray An array of data produced from a line of text by processLine
    * @return
    */
  def processLineAsArray(lineAsArray: Array[Option[(String, Int)]]): Option[List[(Int, Result, Int)]] = {
    lineAsArray match {
      case Array(Some((team1Name, team1Score)), Some((team2Name, team2Score))) =>
        Some(List(
          (dao.getTeam(team1Name).id, Result.getResult(team1Score, team2Score), team1Score),
          (dao.getTeam(team2Name).id, Result.getResult(team2Score, team1Score), team2Score)
        ))
      case _ =>
        None
    }
  }
}
