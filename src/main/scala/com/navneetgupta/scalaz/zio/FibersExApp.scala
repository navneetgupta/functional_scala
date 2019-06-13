package com.navneetgupta.scalaz.zio

import scalaz.zio.console._
import scalaz.zio.{App, IO}

object FibersExApp {
  // Fibers Light weight mechanisim for concurrency like thread/ actor..

  // Perform an action without blocking the current process.

  // fork any IO[E, A] to immediately yield an IO[Nothing, Fiber[E, A]]

  def analyzeData[A, B, E](value: A): IO[E, B] = ???

  def validateData[A, E](value: A): IO[E, Boolean] = ???

  def getData[A](): A = ???

  val data = getData()

  val analyzed =
    for {
      fiber1 <- analyzeData(data).fork // IO[E, Analysis]
      fiber2 <- validateData(data).fork // IO[E, Boolean]
      // Do other stuff
      valid <- fiber2.join
      _ <- if (!valid) fiber1.interrupt
      else IO.unit
      analyzed <- fiber1.join
    } yield analyzed
}

object FibersFibEx {
  def fib(n: Int): IO[Nothing, Int] = {
    if (n <= 0) IO.succeedLazy(1)
    else {
      for {
        fiber1 <- fib(n - 2).fork
        fiber2 <- fib(n - 1).fork
        v2 <- fiber2.join
        v1 <- fiber1.join
      } yield v1 + v2
    }
  }

  def test() = {
    for {
      f1 <- fib(1)
      _ <- putStrLn(s" Fibonacci 1 is ${f1}")
      f2 <- fib(2)
      _ <- putStrLn(s" Fibonacci 2 is ${f2}")
      f3 <- fib(3)
      _ <- putStrLn(s" Fibonacci 3 is ${f3}")
      f4 <- fib(4)
      _ <- putStrLn(s" Fibonacci 4 is ${f4}")
      f5 <- fib(5)
      _ <- putStrLn(s" Fibonacci 5 is ${f5}")
      f6 <- fib(6)
      _ <- putStrLn(s" Fibonacci 6 is ${f6}")
      f7 <- fib(7)
      _ <- putStrLn(s" Fibonacci 7 is ${f7}")
      f8 <- fib(8)
      _ <- putStrLn(s" Fibonacci 8 is ${f8}")
      f9 <- fib(9)
      _ <- putStrLn(s" Fibonacci 9 is ${f9}")
      f10 <- fib(10)
      _ <- putStrLn(s" Fibonacci 10 is ${f10}")
    } yield ()
  }

  //  Two IO actions can be raced, which means they will be executed in parallel, and the value of the first action that completes successfully will be returned.

  //  println(fib(100) race fib(200))
  //    The race combinator is resource-safe, which means that if one of the two actions returns a value, the other one will be interrupted,
  //  to prevent wasting resources.

  //  The race and even par combinators are a specialization of a much-more powerful combinator called raceWith,
  //  which allows executing user-defined logic when the first of two actions succeeds.

  //  The par combinator has resource-safe semantics. If one computation fails, the other computation will be interrupted, to prevent wasting resources.
}

object FibersFibExApp extends App {
  def run(args: List[String]) =
    FibersFibEx.test().fold(_ => 1, _ => 0)
}