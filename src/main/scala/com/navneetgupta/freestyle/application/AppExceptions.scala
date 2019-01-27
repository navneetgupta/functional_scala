package com.navneetgupta.freestyle.application

import cats.data.NonEmptyList

object AppExceptions {
  sealed abstract class AppExceptions(msg: String) extends Exception(msg)

  case class CustomerNotFound(id: CustomerID) extends AppExceptions(s"Customer not found with ID: $id")
  case class QuantityNotAvailable(error: String) extends AppExceptions(error)
  case class ValidationError(errors: NonEmptyList[String]) extends AppExceptions(errors.reduceLeft(_ + "\n" +_))
}
