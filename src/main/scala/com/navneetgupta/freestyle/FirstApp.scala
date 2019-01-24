package com.navneetgupta.freestyle

import freestyle.free._
import freestyle.free.implicits._

object First {

  // FS[_], used in the smart constructors, as FS[_] = FreeS.Par[F], which in turn is an alias for Free[FreeApplicative[F, ?], ?]
  @free trait Validation {
      def minSize(s: String, n: Int): FS[Boolean]
      def hasNumber(s: String): FS[Boolean]
    }



  @free trait Interaction {
    def tell(msg: String): FS[Unit]
    def ask(prompt: String): FS[String]
  }

  @module trait Application {
      val validation: Validation
      val interaction: Interaction

      import cats.implicits._

      def program: FS.Seq[Unit] =
        for {
          userInput <- interaction.ask("Give me something with at least 3 chars and a number on it")
          valid     <- (validation.minSize(userInput, 3), validation.hasNumber(userInput)).mapN(_ && _).freeS
          _         <- if (valid)
            interaction.tell("awesomesauce!")
          else
            interaction.tell(s"$userInput is not valid")
        } yield ()

    }
}


object FirstApp extends App {
  import First._
  import cats.effect.IO
  import cats.effect.implicits._
  import cats.implicits._

  implicit val ValidationIOHandler = new Validation.Handler[IO] {
    override def minSize(s: String, n: Int) = IO.pure(s.length > n)
    override def hasNumber(s: String) : IO[Boolean] = IO{s.exists(c => "0123456789".contains(c))}
  }

  implicit val InteractionHandler = new Interaction.Handler[IO] {
    override def tell(msg: String) = IO{println(msg)}
    override def ask(prompt: String) = IO {println(prompt); scala.io.StdIn.readLine()}
  }

  Application.instance.program.interpret[IO].unsafeRunSync
}