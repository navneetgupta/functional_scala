package com.navneetgupta.cats.effects.datatypes

import cats.effect.IO

import scala.concurrent.ExecutionContext

object IOErrorHandling extends App {
  val boom = IO.raiseError(new Exception("boom"))
  // boom: cats.effect.IO[Nothing] = IO(throw java.lang.Exception: boom)


  println(boom.attempt.unsafeRunSync())
  //  boom.unsafeRunSync()

  import cats.effect._
  import cats.syntax.all._
  import scala.concurrent.duration._


  // Expotential Back-off
  def retryWithBackoff[A](ioa: IO[A], initialDelay: FiniteDuration, maxRetries: Int)
                         (implicit timer: Timer[IO]): IO[A] = {

    ioa.handleErrorWith { error =>
      if (maxRetries > 0) {
        println(s"Retrying ${maxRetries}")
        IO.sleep(initialDelay) *> retryWithBackoff(ioa, initialDelay * 2, maxRetries - 1)
      } else
        IO.raiseError(error)
    }
  }


  implicit val timer = IO.timer(ExecutionContext.global)
  retryWithBackoff(boom, 1.second, 3).attempt.unsafeRunSync()
}
