package com.navneetgupta.scalaz.zio

import scalaz.zio.{IO, RTS}
import java.io.File
import java.io.IOException
import scalaz.zio.console._

import org.apache.commons.io.FileUtils

object LiftPureValues extends App {
  val rts = new RTS {}
  val p: IO[Nothing, String] = IO.point("Hello World!!!!")

  //  The constructor uses non-strict evaluation, so the parameter will not be evaluated until
  //  when and if the IO action is executed at runtime, which is useful if the construction is
  //  costly and the value may never be needed.

  //  Alternately, you can use the IO.now constructor to perform strict evaluation of the value:

  val p1 = IO.now("Hello World Strictly!!!!!!!")


  //  use the sync method of IO to import effectful synchronous code into your purely functional program:

  val z: IO[Nothing, Long] = IO.sync(System.nanoTime())

  // if effectful code can throw Exceptions.

  def readFile(name: String): IO[Exception, Array[Byte]] =
    IO.syncException(FileUtils.readFileToByteArray(new File(name)))


  // To catch any Throwable

  def readFile2(name: String): IO[String, Array[Byte]] =
    IO.syncCatch(FileUtils.readFileToByteArray(new File(name))) {
      case e: IOException => "Could not read file"
    }

  // Use async method of IO to import effectful asynchronous code into your purely functional program:

  //  def makeRequest(req: Request): IO[HttpException, Response] =
  //    IO.async[HttpException, Response](cb => Http.req(req, cb))


  // mapping IO[E,A]  to IO[E, B]

  val z1: IO[Nothing, Int] = IO.point("2323").map(_.toInt * 10)

  // transfrom IO[E, A] to IO[E2, A] using leftMap

  val z2: IO[Exception, String] = IO.fail("No no!").leftMap(msg => new Exception(msg)) // IO[String, A] to IO[Exception, A]

  //  Chaining the Request

  val z3: IO[Nothing, List[Int]] = IO.point(List(1, 2, 3)).flatMap { list =>
    IO.point(list.map(_ + 1))
  }


  val z4: IO[Nothing, List[Int]] = for {
    list <- IO.point(List(1, 2, 3))
    added <- IO.point(list.map(_ + 1))
  } yield added

  rts.unsafeRun(for {
    a <- p
    _ <- putStrLn(a)
    b <- p1
    _ <- putStrLn(b)
    c <- z1
    _ <- putStrLn(c.toString)
    //    d <- z2
    //    _ <- putStrLn(d)
    e <- z3
    _ <- putStrLn(e.toString)
    f <- z4
    _ <- putStrLn(f.toString)
  } yield ())

}
