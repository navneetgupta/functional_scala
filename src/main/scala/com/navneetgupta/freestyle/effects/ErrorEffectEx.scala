package com.navneetgupta.freestyle.effects

import freestyle.free._
import freestyle.free.effects.error.ErrorM
import cats.implicits._
import freestyle.free.effects.error.implicits._
import freestyle.free.implicits._

object ErrorEffectEx extends App {
  //allows the short circuiting of programs and handling invocations which can potentially result in runtime exceptions.
  // It includes three basic operations either, error, and catchNonFatal

  //either allows us to lift values of Either[Throwable, ?] into the context of FreeS,
  // raising an error causing the program to short circuit if the value is a Left(throwable) or
  // continuing with the computation in the case of a Right(a).

  val boom = new RuntimeException("BOOM")

  type Target[A] = Either[Throwable, A]

  def shortCircuit[F[_]: ErrorM] =
    for {
      a <- FreeS.pure(1)
      b <- ErrorM[F].either[Int](Left(boom))
      c <- FreeS.pure(1)
    } yield a + b + c

  println(shortCircuit[ErrorM.Op].interpret[Target])


  def continueWithRightValue[F[_]: ErrorM] =
    for {
      a <- FreeS.pure(1)
      b <- ErrorM[F].either[Int](Right(1))
      c <- FreeS.pure(1)
    } yield a + b + c

  println(continueWithRightValue[ErrorM.Op].interpret[Target])


  // to simply raise an error without throwing an exception, use error operation which short circuits the program.
  def shortCircuitWithError[F[_]: ErrorM] =
    for {
      a <- FreeS.pure(1)
      b <- ErrorM[F].error[Int](boom)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(shortCircuitWithError[ErrorM.Op].interpret[Target])

  // CATCHNONFATAL   :: Not all subclasses of java.lang.Throwable are captured by catchNonFatal,
  // as its name implies just those that are considered in scala.util.control.NonFatal.
  //
  //catchNonFatal expects a cats.Eval value which holds a lazy computation


  import cats.Eval

  def catchingExceptions[F[_]: ErrorM] =
    for {
      a <- FreeS.pure(1)
      b <- ErrorM[F].catchNonFatal[Int](Eval.later(throw new RuntimeException))
      c <- FreeS.pure(1)
    } yield a + b + c


  println(catchingExceptions[ErrorM.Op].interpret[Target])
}
