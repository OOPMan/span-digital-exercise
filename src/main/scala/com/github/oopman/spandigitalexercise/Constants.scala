package com.github.oopman.spandigitalexercise

object Constants {
  val dbUriPrefix = "jdbc:h2"
  val defaultDbUriSuffix = ":mem:spandigitalexercise"

  object Result extends Enumeration {
    type Result = Value
    val Win, Loss, Draw = Value
  }
}
