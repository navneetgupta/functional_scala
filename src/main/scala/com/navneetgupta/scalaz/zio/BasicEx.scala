package com.navneetgupta.scalaz.zio

import scalaz.zio.console._
import scalaz.zio.{ IO, App }

object BasicEx {

  def main2() = {
    for {
      _ <- putStrLn("Please Enter Your Name")
      name <- getStrLn
      _ <- putStrLn(s"Welcome Dear $name")
    } yield ()
  }

  def runInParallel[E, A, B](
    leftIO: IO[E, A],
    rightIO: IO[E, B]) = {
    for {
      leftBinder <- leftIO.fork
      rightBinder <- rightIO.fork
      a <- leftBinder.join
      b <- rightBinder.join
    } yield (a, b)
  }
}

object BasicExApp extends App {
  def run(args: List[String])=
    BasicEx.main2().fold(_ => 1, _ => 0)
}
