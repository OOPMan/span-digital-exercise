package com.github.oopman.spandigitalexercise

object Constants {
  val dbUriPrefix = "jdbc:h2"
  val defaultDbUriSuffix = ":mem:spandigitalexercise"

  object Result extends Enumeration {
    type Result = Value
    val Win, Loss, Draw = Value

    /**
      * Given two score values, determine whether the Result of the 1st vs the 2nd
      *
      * @param score1 Primary score value
      * @param score2 Opposing score value
      * @return
      */
    def getResult(score1: Int, score2: Int): Result = {
      if (score1 > score2) Win
      else if (score2 > score1) Loss
      else Draw
    }
  }
}
