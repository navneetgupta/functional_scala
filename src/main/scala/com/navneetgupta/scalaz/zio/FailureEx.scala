package com.navneetgupta.scalaz.zio

import java.io.IOException

import scalaz.zio.console._
import scalaz.zio.{IO, RTS}


object FailureEx {

  // Like all IO values, these are immutable values and do not actually throw any exceptions;
  // They merely describe failure as a first-class value.

  val z: IO[String, String] = IO.fail(error = "Failing Intentionally")

  //surface failures with attempt, which takes an IO[E, A] and produces an IO[E2, Either[E, A]].
  // The choice of E2 is unconstrained, because the resulting computation cannot fail with any error.

  readUrls("data.json").attempt.map {
    case Left(_) => "42"
    case Right(data) => data
  }


  // Submerge failures with IO.absolve, which is the opposite of attempt and turns an IO[E, Either[E, A]] into an IO[E, A]:

  def sqrt(io: IO[Nothing, Double]): IO[String, Double] =
    IO.absolve(
      a1(io)
    )

  def a1(io: IO[Nothing, Double]): IO[Nothing, Either[String, Double]] =
    io.map(value =>
      if (value < 0.0) Left("Value must be >= 0.0")
      else Right(Math.sqrt(value))
    )


  val z1: IO[IOException, Array[Byte]] = openFile("primary.json").catchAll(_ => openFile("backup.json"))

  def openFile[E, A](fileName: String): IO[E, A] = ???

  val z2: IO[IOException, Array[Byte]] = openFile("primary.json").catchSome {
    case _: java.io.FileNotFoundException => openFile("backup.json")
  }

  //    You can execute one action, or, if it fails, execute another action, with the orElse combinator:

  val z3: IO[IOException, Array[Byte]] = openFile("primary.json").orElse(openFile("backup.json"))

  //  If you want more control on the next action and better performance you can use the primitive which all the previous operations are based on,
  //  it’s called redeem and it can be seen as the combination of flatMap and catchAll. It is useful if you find yourself using combinations of attempt
  //  or catchAll with flatMap, using redeem you can achieve the same and avoid the intermediate Either allocation and the subsequent call to flatMap.

  val z4: IO[Nothing, Content] =
    readUrls("urls.json").redeem(e => IO.point(NoContent(e)), fetchContent)

  def readUrls[E, A](str: String): IO[E, A] = ???

  def fetchContent[E, A, B](a: A): IO[E, B] = ???

  sealed trait Content

  case class NoContent[A](value: A) extends Content


}

object FailureExApp extends App {
  val rts = new RTS {}


  def sqrt(io: IO[Nothing, Double]): IO[String, Double] =
    IO.absolve(
      a1(io)
    )

  def a1(io: IO[Nothing, Double]): IO[Nothing, Either[String, Double]] =
    io.map(value =>
      if (value < 0.0) Left("Value must be >= 0.0")
      else Right(Math.sqrt(value))
    )

  rts.unsafeRun(
    for {
      sqrt5 <- sqrt(IO.point(5.0))
      _ <- putStrLn(s"sqrt of 5 is ${sqrt5}")
    } yield ())


}
