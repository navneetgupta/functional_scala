package com.navneetgupta.scalaz.zio

import scalaz.zio.IO

object FailureEx extends App {

  // Like all IO values, these are immutable values and do not actually throw any exceptions;
  // They merely describe failure as a first-class value.

  val z: IO[String, String] = IO.fail(error = "Failing Intentionally")



}
