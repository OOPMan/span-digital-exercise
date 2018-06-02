package com.github.oopman.spandigitalexercise

import java.io.File

import scala.io.Source
import io.getquill.NamingStrategy
import com.github.oopman.spandigitalexercise.Constants.Result
import io.getquill.context.sql.idiom.SqlIdiom

import scala.util.matching.Regex

/**
  * This class encapsulates the logic involved in ingesting input data for Teams
  * and Results
  *
  * @param dao Instance of DAO
  * @tparam N NamingStrategy type associated with DAO instance
  */
class Ingester[Dialect <: SqlIdiom, Naming <: NamingStrategy](dao: DAO[Dialect, Naming]) {
  val resultPattern: Regex = raw"(.+) (\d+)".r
  /**
    * Ingest data from a Sequence of File instances, returning the count of
    * Files successfully ingested
    *
    * @param files A Sequence of Files
    * @return Number of files successfully ingested
    */
  def ingestFiles(files: Seq[File]): Int = files.map(ingestFile).count(_ == true)

  /**
    * Ingest data from a single File instance, returning true in the instance of
    * a successful ingestion
    *
    * @param file A File to ingest
    * @return
    */
  def ingestFile(file: File): Boolean = {
    val lines = Source.fromFile(file).getLines
    val results = lines
      // Step 1: Convert lines into Arrays of Options of 2-tuples containing
      // Team Name and Score
      .map(_.trim.split(',').map {
        case resultPattern(teamName, score) => Some((teamName, score.toInt))
        case _ => None
      })
      // Step 2: Convert Arrays of 2-tuples into Lists of Options of 3-tuples
      // containing Team PK and Result enum
      .map {
        case Array(Some((team1Name, team1Score)), Some((team2Name, team2Score))) =>
          Some(List(
            (dao.getTeam(team1Name).id, Result.getResult(team1Score, team2Score), team1Score),
            (dao.getTeam(team2Name).id, Result.getResult(team2Score, team1Score), team2Score)
          ))
        case _ => None
      }
      // Step 3: Remove any Nones, flatten and insert Result objects
      .filter(_.isDefined)
      .flatMap(_.get)
      .map {
        case (teamId, result, score) => dao.addResult(teamId, result, score)
      }
    results.sum > 0
  }

}
