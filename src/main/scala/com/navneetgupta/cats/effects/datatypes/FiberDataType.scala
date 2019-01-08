package com.navneetgupta.cats.effects.datatypes

import cats.effect._

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._

import scala.concurrent.ExecutionContext


object FiberDataType extends App {
  // Represents the (pure) result of an Async data type (e.g. IO) being started concurrently, that can be either joined or canceled.

  // Like lightweight threads, a fiber being a concurrency primitive for doing cooperative multi-tasking.

  // fibers as being lightweight threads, a fiber being the pure and light equivalent of a thread
  // that can be either joined (via join) or interrupted (via cancel).

  implicit val context = IO.contextShift(global)
  val io = IO {
    println("Hello There!!!")
  }

  val fiber = io.start


  val launchMissiles = IO.raiseError(new Exception("Boom!!! Boom !!!"))
  val returnToBuker: IO[Unit] = IO{println("Returned to Bunker")}

  val res = for {
    fiber <- launchMissiles.start
    _ <- returnToBuker.handleErrorWith { e =>
      fiber.cancel *> IO.raiseError(e)
    }
    a <- fiber.join
  } yield a


  println(res.attempt.unsafeRunSync())


}
