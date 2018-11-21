package com.navneetgupta.learning.scalaz

object SumTypes {
 //implemeted using sealed trait/ sealed abstract

  sealed abstract class Status
  case object Opening extends Status
  case object Opened extends Status
  case object Verify extends Status
  case object Active extends Status
  case object InActive extends Status
  case object Closed extends Status
}
