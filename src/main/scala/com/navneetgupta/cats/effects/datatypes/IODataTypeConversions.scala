package com.navneetgupta.cats.effects.datatypes

import cats.effect.IO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IODataTypeConversions extends App {
  // Conversions

  // Lazy evaluation, equivalent with by-name parameters:
  println(IO.fromFuture(IO {
    Future(println("I come from the Future!"))
  }).unsafeRunSync())

  //Eager evaluation:

  val f = Future.successful("I come from the Future!")

  println(IO.fromFuture(IO.pure(f)).unsafeRunSync())


  // From error
  def fromEither[A](e: Either[Throwable, A]): IO[A] = e.fold(IO.raiseError, IO.pure)

  println(fromEither(Right(10)).unsafeRunSync())

  println(fromEither(Left(new Exception("Boom !!!"))).attempt.unsafeRunSync())
  println(fromEither(Left(new Exception("Boom !!!"))).unsafeRunSync())
}
