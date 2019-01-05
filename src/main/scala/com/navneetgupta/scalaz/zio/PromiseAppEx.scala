package com.navneetgupta.scalaz.zio

import java.io.IOException

import scalaz.zio.console._
import scalaz.zio.duration._
import scalaz.zio.{IO, Promise, RTS}


object PromiseAppEx extends App {

  val ioPromise = Promise.make[Exception, String]
  val ioCompleted =
    ioPromise.flatMap(p => p.complete("I'm Done Bro...."))


  println(ioCompleted)

  val ioPromise2 = Promise.make[Exception, Nothing]
  val ioException = ioPromise2.flatMap(promise => promise.error(new Exception("Error....")))

  val ioPromise3 = Promise.make[Exception, String]
  val ioGet = ioPromise3.flatMap(promise => promise.get)

  val ioPromise4 = Promise.make[Exception, String]
  val ioDone = ioPromise4.flatMap(promise => promise.poll)


  val program: IO[IOException, Unit] = for {
    promise <- Promise.make[Nothing, String]
    sendHelloWorld = (IO.now("hello world") <* IO.sleep(1.second))
      .flatMap(promise.complete)
    getAndPrint = promise.get.flatMap(putStrLn)
    fiberA <- sendHelloWorld.fork
    fiberB <- getAndPrint.fork
    _ <- (fiberA zip fiberB).join
  } yield ()

  val rts = new RTS {}

  rts.unsafeRun(for {
    res <- ioCompleted
    _ <- putStrLn(s"does ioFiber Completed : ${res}")
    _ <- program
  } yield ())
}
