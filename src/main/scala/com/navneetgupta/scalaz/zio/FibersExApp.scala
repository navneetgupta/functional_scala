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

object FibersFibEx extends App {
  def fib(n :Int) : IO[Nothing, Int] = {
    if (n <= 0) IO.point(1)
    else {
      for {
        fiber1 <- fib(n-2).fork
        fiber2 <- fib(n-1).fork
        v2 <- fiber2.join
        v1 <- fiber1.join
      } yield v1+v2
    }
  }

  println(fib(4))
  println(fib(3))
  println(fib(2))
  println(fib(1))
  println(fib(0))
  println(fib(5))
  println(fib(6))
  println(fib(7))
  println(fib(8))

  //  Two IO actions can be raced, which means they will be executed in parallel, and the value of the first action that completes successfully will be returned.

  println(fib(100) race fib(200))
  //  The race combinator is resource-safe, which means that if one of the two actions returns a value, the other one will be interrupted,
  //  to prevent wasting resources.

  //  The race and even par combinators are a specialization of a much-more powerful combinator called raceWith,
  //  which allows executing user-defined logic when the first of two actions succeeds.

  //  The par combinator has resource-safe semantics. If one computation fails, the other computation will be interrupted, to prevent wasting resources.
}