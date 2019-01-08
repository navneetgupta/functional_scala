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

}

