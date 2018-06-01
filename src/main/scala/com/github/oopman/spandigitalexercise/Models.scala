package com.github.oopman.spandigitalexercise

import com.github.oopman.spandigitalexercise.Constants.Result.Result


object Models {
  /**
    * Model class representing Teams
    * @param id PK
    * @param name Team name
    */
  case class Teams(id: Int, name: String)

  /**
    * Model class representing match results
    *
    * @param id PK
    * @param teamId PK of Team whose result this object is
    * @param result Match result for Team
    * @param score Match score for Team
    */
  case class Results(id: Int,
                     teamId: Int,
                     result: Result,
                     score: Int)
}
