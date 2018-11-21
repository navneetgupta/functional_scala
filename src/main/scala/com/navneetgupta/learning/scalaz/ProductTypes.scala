package com.navneetgupta.learning.scalaz

object ProductTypes {
  import SumTypes._
  case class Person(
      name: String,
      age: Integer,
      email: String,
      emailStatus: Status
  )

  case class BaseId(value: String) extends AnyVal //Value Class No run-time overhead.
}
