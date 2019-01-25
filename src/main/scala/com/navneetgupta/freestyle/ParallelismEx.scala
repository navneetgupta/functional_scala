package com.navneetgupta.freestyle

import freestyle.free._
import freestyle.free.nondeterminism._

object ParallelismEx {

}

object ParallelismExAlgebra {
  @free trait Validation {
    def minSize(n: Int): FS[Boolean]
    def hasNumber: FS[Boolean]
  }
}

object ParallelismExApp extends App {
  import ParallelismExAlgebra._
  import cats.data.Kleisli
  import cats.syntax.apply._
  import scala.concurrent.Future
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  type ParValidator[A] = Kleisli[Future, String, A]

  implicit val interpreter = new Validation.Handler[ParValidator] {
    override def minSize(n: Int): ParValidator[Boolean] =
      Kleisli(s => Future(s.size >= n))

    override def hasNumber: ParValidator[Boolean] =
      Kleisli(s => Future(s.exists(c => "0123456789".contains(c))))
  }

  val validation = Validation[Validation.Op]
  // validation: Validation[Validation.Op] = Validation$To@7c2b697a

  import validation._
  // import validation._

  val parValidation = (minSize(3), hasNumber).mapN(_ :: _ :: Nil)
  val validator = parValidation.interpret[ParValidator]


  validator.run("abc1").onComplete(println(_))
}