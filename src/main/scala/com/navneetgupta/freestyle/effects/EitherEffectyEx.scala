package com.navneetgupta.freestyle.effects

import freestyle.free._
import freestyle.free.effects.either

object EitherEffectyEx extends App {
  // effect allows short circuiting of programs and handling invocations which can potentially result in runtime
  // exceptions and that can be translated to a custom left value. It includes three basic operations either,
  // error, and catchNonFatal

  //either allows us to lift values of Either[Throwable, ?] into the context of FreeS,
  // raising an error causing the program to short circuit if the value is a Left(throwable) or
  // continuing with the computation in the case of a Right(a).

  sealed trait BizException

  case object Biz1 extends BizException

  val e = either[BizException]

  import e.implicits._

  import cats.implicits._

  type Target[A] = Either[BizException, A]

  def shortCircuit[F[_]: e.EitherM] =
    for {
      a <- FreeS.pure(1)
      b <- e.EitherM[F].either[Int](Left(Biz1))
      c <- FreeS.pure(1)
    } yield a + b + c

  println(shortCircuit[e.EitherM.Op].interpret[Target])
  def continueWithRightValue[F[_]: e.EitherM] =
    for {
      a <- FreeS.pure(1)
      b <- e.EitherM[F].either[Int](Right(1))
      c <- FreeS.pure(1)
    } yield a + b + c

  println(continueWithRightValue[e.EitherM.Op].interpret[Target])

  def shortCircuitWithError[F[_]: e.EitherM] =
    for {
      a <- FreeS.pure(1)
      b <- e.EitherM[F].error[Int](Biz1)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(shortCircuitWithError[e.EitherM.Op].interpret[Target])

  import cats.Eval

  def catchingExceptions[F[_]: e.EitherM] =
    for {
      a <- FreeS.pure(1)
      b <- e.EitherM[F].catchNonFatal[Int](Eval.later(throw new RuntimeException), _ => Biz1)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(catchingExceptions[e.EitherM.Op].interpret[Target])
}
