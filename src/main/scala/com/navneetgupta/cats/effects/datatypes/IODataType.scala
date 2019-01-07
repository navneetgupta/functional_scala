package com.navneetgupta.cats.effects.datatypes

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object IODataType extends App {
  // Effectful results are not memoized,
  val ioa = IO { println("Hello!! IO") }
  val io: IO[Unit] = for {
    _ <- ioa
    _ <- ioa
  } yield ()

  io.unsafeRunSync() // will print two times Hello!! due to not memoized result

  implicit val ec = ExecutionContext.global
  val futurea = Future { println("Hello!! Future")}

  val future: Future[Unit] = for {
    _ <- futurea
    _ <- futurea
  } yield ()

  future.value    // will print Hello!! only one times

  // IO flatMap is trampolined => Stack safety

  def fib(n :Int, a: Long = 0, b: Long = 1) : IO[Long] =
    for {
      b2 <- IO(a+b)
      res <- if(n > 0) fib(n-1, b, b2)
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
  println(fib(7).unsafeRunSync())
  println(fib(8).unsafeRunSync())
  println(fib(9).unsafeRunSync())
  println(fib(10).unsafeRunSync())
  println(fib(11).unsafeRunSync())
  println(fib(12).unsafeRunSync())
  println(fib(13).unsafeRunSync())
  println(fib(14).unsafeRunSync())
  println(fib(15).unsafeRunSync())
  println(fib(150).unsafeRunSync())
  println(fib(151).unsafeRunSync())
  println(fib(152).unsafeRunSync())
  println(fib(1000).unsafeRunSync())
  println(fib(1001).unsafeRunSync())
  println(fib(1002).unsafeRunSync())


}

