package com.navneetgupta.freestyle

import cats._
import freestyle.tagless._
import cats.implicits._

import scala.util.Try

object TaglessEx  {

  @tagless(true) trait Validation {
      def minSize(s: String, n: Int): FS[Boolean]
      def hasNumber(s: String): FS[Boolean]
    }

  @tagless(true) trait Interaction {
    def tell(msg: String): FS[Unit]
    def ask(prompt: String): FS[String]
  }

  def taglessProgram[F[_]:Monad]
  (implicit
   validation : Validation[F],
   interaction: Interaction[F]) = {

    for {
      userInput <- interaction.ask("Give me something with at least 3 chars and a number on it")
      valid <- (validation.minSize(userInput, 3), validation.hasNumber(userInput)).mapN(_ && _)
      _ <- if (valid)
        interaction.tell("awesomesauce!")
      else
        interaction.tell(s"$userInput is not valid")
    } yield ()
  }
}

object TaglessExIOApp extends App {
  import TaglessEx._
  import cats.effect.IO

  implicit val ValidationIOHandler = new TaglessEx.Validation.Handler[IO] {
    override def minSize(s: String, n: Int) = IO.pure(s.length > n)
    override def hasNumber(s: String) : IO[Boolean] = IO{s.exists(c => "0123456789".contains(c))}
  }

  implicit val InteractionHandler = new TaglessEx.Interaction.Handler[IO] {
    override def tell(msg: String) = IO{println(msg)}
    override def ask(prompt: String) = IO {println(prompt); scala.io.StdIn.readLine()}
  }

  taglessProgram[IO].unsafeRunSync

}

// Not Stack Safe as Try is not stack safe.we can still execute our program stack safe with Freestyle by interpreting to Free[Try, ?] instead of Try directl
object TaglessExTryApp extends App {

  implicit val ValidationIOHandler = new TaglessEx.Validation.Handler[Try] {
    override def minSize(s: String, n: Int) = Try {s.length >= n}
    override def hasNumber(s: String) : Try[Boolean] = Try{s.exists(c => "0123456789".contains(c))}
  }

  implicit val InteractionHandler = new TaglessEx.Interaction.Handler[Try] {
    override def tell(msg: String) = Try{println(msg)}
    override def ask(prompt: String) = Try {"This Could be a User input 1"}
  }

  TaglessEx.taglessProgram[Try]
}

object TaglessExTrySafeApp extends App {
  import cats.free.Free
  
  implicit val ValidationIOHandler = new TaglessEx.Validation.Handler[Try] {
    override def minSize(s: String, n: Int) = Try {s.length >= n}
    override def hasNumber(s: String) : Try[Boolean] = Try{s.exists(c => "0123456789".contains(c))}
  }

  implicit val InteractionHandler = new TaglessEx.Interaction.Handler[Try] {
    override def tell(msg: String) = Try{println(msg)}
    override def ask(prompt: String) = Try {"This Could be a User input 1"}
  }
  TaglessEx.taglessProgram[Free[Try, ?]].runTailRec
}