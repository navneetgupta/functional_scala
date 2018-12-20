package com.navneetgupta.scalaz.zio

import scalaz.zio.IO

object FibersExApp extends App{
  // Fibers Light weight mechanisim for concurrency like thread/ actor..

  // Perform an action without blocking the current process.

  // fork any IO[E, A] to immediately yield an IO[Nothing, Fiber[E, A]]

  def analyzeData[A, B, E](value: A) : IO[E, B] = ???
  def validateData[A, E](value: A): IO[E, Boolean] = ???
  def getData[A](): A = ???
  val data = getData()

  val analyzed =
    for {
      fiber1   <- analyzeData(data).fork  // IO[E, Analysis]
      fiber2   <- validateData(data).fork // IO[E, Boolean]
      // Do other stuff
      valid    <- fiber2.join
      _        <- if (!valid) fiber1.interrupt
      else IO.unit
      analyzed <- fiber1.join
    } yield analyzed
  
}
