package com.navneetgupta.freestyle

import cats.effect.IO
import freestyle.tagless.tagless
import freestyle.free._
import freestyle.free.logging._
import freestyle.free.loggingJVM.journal.implicits._
import freestyle.tagless._
import freestyle.free.implicits._

object TaglessEx  {

  @tagless(true) trait Validation {
      def minSize(s: String, n: Int): FS[Boolean]
      def hasNumber(s: String): FS[Boolean]
    }

  @tagless(true) trait Interaction {
    def tell(msg: String): FS[Unit]
    def ask(prompt: String): FS[String]
  }

  def program[F[_]]
  (implicit log: LoggingM[F],
   validation : Validation.StackSafe[F],
   interaction: Interaction.StackSafe[F]) = {

    import cats.implicits._

    for {
      userInput <- interaction.ask("Give me something with at least 3 chars and a number on it")
      valid <- (validation.minSize(userInput, 3), validation.hasNumber(userInput)).mapN(_ && _)
      _ <- if (valid)
        interaction.tell("awesomesauce!")
      else
        interaction.tell(s"$userInput is not valid")
      _ <- log.debug("Program finished")
    } yield ()
  }
  // pr


}

object TaglessExApp extends App {
//  import TaglessEx._
//  implicit val ValidationIOHandler = new Validation.StackSafe[IO] {
//    override def minSize(s: String, n: Int) = IO.pure(s.length > n)
//    override def hasNumber(s: String) : IO[Boolean] = IO{s.exists(c => "0123456789".contains(c))}
//  }
//
//  implicit val InteractionHandler = new Interaction.StackSafe[IO] {
//    override def tell(msg: String) = IO{println(msg)}
//    override def ask(prompt: String) = IO {println(prompt); scala.io.StdIn.readLine()}
//  }
//  program.interpret[IO].unsafeRunSync



}