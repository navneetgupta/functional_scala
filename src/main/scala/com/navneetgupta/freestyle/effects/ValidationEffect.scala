package com.navneetgupta.freestyle.effects

import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.effects.validation
import cats.data.State
import cats.implicits._
import cats.mtl.implicits._
import cats.instances.list._

object ValidationEffectEx extends App {
  // Validation effect allows for the distinction between valid and invalid values in a program,
  // accumulating the validation errors when executing it.
  // validation effect, like state, supports parameterization to any type remaining type safe throughout the program declaration.
  // There needs to be implicit evidence of cats.mtl.MonadState[M[_], List[E]] for any runtime M[_] where E is the
  // type of the validation error due to the constraints placed by this effect.

  //  three basic operations valid, invalid, and errors.
  //  Apart from these, it includes a couple of combinators for accumulating errors: fromEither and fromValidatedNel

  sealed trait ValidationError
  case class NotValid(explanation: String) extends ValidationError

  val vl = validation[ValidationError]

  import vl.implicits._

  type ValidationResult[A] = State[List[ValidationError], A]

  def programValid[F[_]: vl.ValidationM] =
    for {
      a <- FreeS.pure(1)
      b <- vl.ValidationM[F].valid(1)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programValid[vl.ValidationM.Op].interpret[ValidationResult].run(List()).value)

  def programInvalid[F[_]: vl.ValidationM] =
    for {
      a <- FreeS.pure(1)
      _ <- vl.ValidationM[F].invalid(NotValid("oh no"))
      b <- FreeS.pure(1)
    } yield a + b

  println(programInvalid[vl.ValidationM.Op].interpret[ValidationResult].run(List()).value)


  def programErrors[F[_]: vl.ValidationM] =
    for {
      _ <- vl.ValidationM[F].invalid(NotValid("oh no"))
      errs <- vl.ValidationM[F].errors
      _ <- vl.ValidationM[F].invalid(NotValid("this won't be in errs"))
    } yield errs

  println(programErrors[vl.ValidationM.Op].interpret[ValidationResult].run(Nil).value)

  //We can interleave Either[ValidationError, ?] values in the program. If they have errors on the left side they will be accumulated:
  def programFromEither[F[_]: vl.ValidationM] =
    for {
      _ <- vl.ValidationM[F].fromEither(Left(NotValid("oh no")) : Either[ValidationError, Int])
      a <- vl.ValidationM[F].fromEither(Right(42) : Either[ValidationError, Int])
    } yield a


  println(programFromEither[vl.ValidationM.Op].interpret[ValidationResult].run(Nil).value)

  import cats.data.{Validated, ValidatedNel, NonEmptyList}

  def programFromValidatedNel[F[_]: vl.ValidationM] =
    for {
      a <- vl.ValidationM[F].fromValidatedNel(
        Validated.Valid(42)
      )
      _ <- vl.ValidationM[F].fromValidatedNel(
        Validated.invalidNel[ValidationError, Unit](NotValid("oh no"))
      )
      _ <- vl.ValidationM[F].fromValidatedNel(
        Validated.invalidNel[ValidationError, Unit](NotValid("another error!"))
      )
    } yield a

  println(programFromValidatedNel[vl.ValidationM.Op].interpret[ValidationResult].run(Nil).value)


  // importing the validation effect implicits, a couple of methods are available for lifting valid and invalid values to our program: liftValid and liftInvalid.
  def programSyntax[F[_]: vl.ValidationM] =
    for {
      a <- 42.liftValid
      _ <- NotValid("oh no").liftInvalid
      _ <- NotValid("another error!").liftInvalid
    } yield a

  println(programSyntax[vl.ValidationM.Op].interpret[ValidationResult].run(Nil).value)
}
