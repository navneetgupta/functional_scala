package com.navneetgupta.scalaz.zio

import java.io.IOException

import scalaz.zio.console._
import scalaz.zio.duration._
import scalaz.zio.clock._
import scalaz.zio.{DefaultRuntime, IO, Promise}

object PromiseAppEx extends App {

  val ioPromise = Promise.make[Exception, String]
  val ioCompleted =
    ioPromise.flatMap(p => p.succeed("I'm Done Bro...."))


  println(ioCompleted)

  val ioPromise2 = Promise.make[Exception, Nothing]
  val ioException = ioPromise2.flatMap(promise => promise.fail(new Exception("Error....")))

  val ioPromise3 = Promise.make[Exception, String]
  val ioGet = ioPromise3.flatMap(promise => promise.await)

  val ioPromise4 = Promise.make[Exception, String]
  val ioDone = ioPromise4.flatMap(promise => promise.poll)


  val program = for {
    promise <- Promise.make[Nothing, String]
    sendHelloWorld = (IO.succeed("hello world") <* sleep(1.second))
      .flatMap(promise.succeed)
    getAndPrint = promise.await.flatMap(putStrLn)
    fiberA <- sendHelloWorld.fork
    fiberB <- getAndPrint.fork
    _ <- (fiberA zip fiberB).join
  } yield ()

  val rts = new DefaultRuntime {}

  rts.unsafeRun(for {
    res <- ioCompleted
    _ <- putStrLn(s"does ioFiber Completed : ${res}")
    _ <- program
  } yield ())
}
