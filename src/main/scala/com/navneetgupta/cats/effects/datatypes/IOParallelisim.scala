package com.navneetgupta.cats.effects.datatypes

import cats.data._
import cats.effect.ExitCase._
import cats.effect.IO

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object IOParallelisim extends App {
  // possible to execute two or more given IOs in parallel.
  //Note: all parallel operations require an implicit ContextShift[IO] in scope


  import cats.syntax.all._

  implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)

  val ioA = IO(println("Running ioA"))
  val ioB = IO(println("Running ioB"))
  val ioC = IO(println("Running ioC"))

  val program = (ioA, ioB, ioC).parMapN { (_, _, _) => () }

  program.unsafeRunSync()

  //  If any of the IOs completes with a failure then the result of the whole computation will be failed,
  //  while the unfinished tasks get cancelled. Example:

  val a = IO.raiseError[Unit](new Exception("boom")) <* IO(println("Running ioA"))

  val b = (IO.sleep(1.second) *> IO(println("Running ioB")))
    .guaranteeCase {
      case Canceled => IO(println("ioB was canceled!"))
      case _ => IO.unit
    }

  val parFailure = (a, b).parMapN { (_, _) => () }

  parFailure.unsafeRunCancelable(_ => ())

  // If one of the tasks fails immediately, then the other gets canceled and the computation completes immediately,
  // so in this example the pairing via parMapN will not wait for 10 seconds before emitting the error

  val ioA1 = IO.sleep(10.seconds) *> IO(println("Delayed!"))
  val ioB1 = IO.raiseError[Unit](new Exception("dummy"))

  (ioA1, ioB1).parMapN((_, _) => ()).unsafeRunCancelable(_ => ())

  // parSequence
  // If you have a list of IO, and you want a single IO with the result list you can use parSequence
  // which executes the IO tasks in parallel.

  val anIO = IO(1)

  val aLotOfIOs =
    NonEmptyList.of(anIO, anIO)

  val ioOfList = aLotOfIOs.parSequence
  println(ioOfList.unsafeRunSync())

  //parTraverse
  //If you have a list of data and a way of turning each item into an IO, but you want a single IO for the results you can use parTraverse to run the steps in parallel.
  //
  val results = NonEmptyList.of(1, 2, 3).parTraverse { i =>
    IO{i}
  }

  println(results)
  println(results.unsafeRunSync())

  //Note that as far as the actual behavior of IO is concerned, something like IO.pure(x).map(f) is equivalent with IO(f(x)) and IO.pure(x).flatMap(f) is equivalent with IO.suspend(f(x)).

}
