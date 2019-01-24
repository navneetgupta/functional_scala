package com.navneetgupta.freestyle

import cats.Monad
import freestyle.free.logging._
import freestyle.free._
import cats.implicits._
import freestyle.tagless.tagless

object TaglessFreeCombinedInterpreterEx {
  @tagless(true) trait Validation {
    def minSize(s: String, n: Int): FS[Boolean]
    def hasNumber(s: String): FS[Boolean]
  }

  @tagless(true) trait Interaction {
    def tell(msg: String): FS[Unit]
    def ask(prompt: String): FS[String]
  }

  def taglessProgram[F[_]:Monad]
  (implicit loggingM : LoggingM[F],
   validation : Validation.StackSafe[F],
   interaction: Interaction.StackSafe[F]) = {

    for {
      userInput <- interaction.ask("Give me something with at least 3 chars and a number on it")
      valid <- (validation.minSize(userInput, 3), validation.hasNumber(userInput)).mapN(_ && _)
      _ <- if (valid)
              interaction.tell("awesomesauce!")
            else
              interaction.tell(s"$userInput is not valid")
      _ <- loggingM.debug("Great its Works")
    } yield ()
  }
}

object TaglessFreeCombinedInterpreterExApp extends App {
  import scala.util.Try
  import freestyle.free.implicits._
  import TaglessFreeCombinedInterpreterEx._

  implicit val ValidationIOHandler = new Validation.Handler[Try] {
    def minSize(s: String, n: Int) = Try {s.length >= n}
    def hasNumber(s: String) : Try[Boolean] = Try{s.exists(c => "0123456789".contains(c))}
  }

  implicit val InteractionHandler = new Interaction.Handler[Try] {
    def tell(msg: String) = Try{println(msg)}
    def ask(prompt: String) = Try {"This Could be a User input 1"}
  }

  @module trait MApp {
        val interaction: TaglessFreeCombinedInterpreterEx.Interaction.StackSafe
        val validation: TaglessFreeCombinedInterpreterEx.Validation.StackSafe
        val loggingM: LoggingM
      }

  import freestyle.free.loggingJVM.journal.implicits._
  TaglessFreeCombinedInterpreterEx.taglessProgram[MApp.Op].interpret[Try]
}
