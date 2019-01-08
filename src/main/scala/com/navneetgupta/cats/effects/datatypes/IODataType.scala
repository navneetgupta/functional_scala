package com.navneetgupta.cats.effects.datatypes

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object IODataType extends App {
  // Effectful results are not memoized,
  val ioa = IO {
    println("Hello!! IO")
  }
  val io: IO[Unit] = for {
    _ <- ioa
    _ <- ioa
  } yield ()

  io.unsafeRunSync() // will print two times Hello!! due to not memoized result

  implicit val ec = ExecutionContext.global
  val futurea = Future {
    println("Hello!! Future")
  }

  val future: Future[Unit] = for {
    _ <- futurea
    _ <- futurea
  } yield ()

  future.value // will print Hello!! only one times

  // IO flatMap is trampolined => Stack safety

  def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
    for {
      b2 <- IO(a + b)
      res <- if (n > 0) fib(n - 1, b, b2)
      else IO.pure(b2)
    } yield res

  //    IO {a + b} .flatMap(
  //      b2 =>
  //        if(n > 0) fib(n-1, b, b2)
  //        else IO.pure(b2)
  //    )


  println(fib(4).unsafeRunSync())
  println(fib(5).unsafeRunSync())
  println(fib(6).unsafeRunSync())


  // Async Effects
  //  via the IO.async and IO.cancelable builders.
  // IO.async is the operation that complies with the laws of Async#async (see Async) and
  // can describe simple asynchronous processes that cannot be `canceled`,

  import scala.concurrent.{Future, ExecutionContext}
  import scala.util.{Success, Failure}

  def convert[A](fa: => Future[A])(implicit ec: ExecutionContext): IO[A] =
    IO.async { cb =>
      fa.onComplete {
        case Success(a) => cb(Right(a))
        case Failure(e) => cb(Left(e))
      }
    }

  println(convert(Future {
    // Some DB Work
    println("Future Value!!")
  }).unsafeRunAsync(a => println {
    a
  }))

  // Synchronous effect: equivalent of Sync[IO].delay, describing IO operations that can be evaluated immediately, on the current thread and call-stack:
  // its execution being “suspended” in the IO context.
  IO{println("Hello!!")}.unsafeRunSync()

  // Deffered Execution IO.suspend

  def fib2(n: Int, a: Long = 0, b: Long = 1) : IO[Long] =
    IO.suspend(
      if(n > 0) fib(n-1, b, a+b)
      else IO.pure(b)
    )
  println("Deffered----------------")
  println(fib2(100).unsafeRunSync())

  import cats.implicits._
  import cats.effect.ContextShift

  def fib3(n: Int, a: Long = 0, b: Long = 1)(implicit cs: ContextShift[IO]): IO[Long] =
    IO.suspend {
      if (n == 0) IO.pure(a) else {
        val next = fib(n - 1, b, a + b)
        // Every 100 cycles, introduce a logical thread fork
        if (n % 100 == 0)
          cs.shift *> next
        else
          next
      }
    }

  implicit val cs = IO.contextShift(ec)
  println(fib3(100).unsafeRunSync())

  // Concurrent start + cancel

  // fibers as being lightweight threads, a fiber being the pure and light equivalent of a thread that can be either joined (via join) or interrupted (via cancel).



  val launchMissiles = IO.raiseError(new Exception("boom!"))
  val runToBunker = IO(println("To the bunker!!!"))

  for {
    fiber <- launchMissiles.start
    _ <- runToBunker.handleErrorWith { error =>
      // Retreat failed, cancel launch (maybe we should
      // have retreated to our bunker before the launch?)
      fiber.cancel *> IO.raiseError(error)
    }
    aftermath <- fiber.join
  } yield {
    aftermath
  }


  // runCancelable & unsafeRunCancelable
  println("Canceabble============")
  import scala.concurrent.duration._

  implicit val timer = IO.timer(ec)

  // Delayed println
  val io1: IO[Unit] = IO.sleep(1.seconds) *> IO(println("Hello!"))

  val cancel: IO[Unit] =
    io1.unsafeRunCancelable(r => println(s"Done: $r"))

  // ... if a race condition happens, we can cancel it,
  // thus canceling the scheduling of `IO.sleep`
  println(cancel.unsafeRunSync())
}

