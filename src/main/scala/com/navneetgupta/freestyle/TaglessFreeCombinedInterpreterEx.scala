package com.navneetgupta.freestyle

import freestyle.free.logging.LoggingM
import freestyle.free._
import cats.implicits._
import freestyle.tagless.tagless

object TaglessFreeCombinedInterpreterEx {

  @tagless(true) trait Validation1 {
    def minSize(s: String, n: Int): FS[Boolean]
    def hasNumber(s: String): FS[Boolean]
  }

  @tagless(true) trait Interaction1 {
    def tell(msg: String): FS[Unit]
    def ask(prompt: String): FS[String]
  }

  def taglessProgram[F[_]]
  (implicit loggingM : LoggingM[F],
   validation : Validation1.StackSafe[F],
   interaction: Interaction1.StackSafe[F]) = {

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
  import freestyle.free.loggingJVM.journal.implicits._
  import cats.implicits._

  implicit val Validation1Handler = new TaglessFreeCombinedInterpreterEx.Validation1.Handler[Try] {
    def minSize(s: String, n: Int) = Try {s.length >= n}
    def hasNumber(s: String) : Try[Boolean] = Try{s.exists(c => "0123456789".contains(c))}
  }

  implicit val Interaction1Handler = new TaglessFreeCombinedInterpreterEx.Interaction1.Handler[Try] {
    def tell(msg: String) = Try{println(msg)}
    def ask(prompt: String) = Try {"This Could be a User input 1"}
  }

  trait MMApp[FF$61325[_]] extends _root_.freestyle.free.internal.EffectLike[FF$61325] {
    val interaction: TaglessFreeCombinedInterpreterEx.Interaction1.StackSafe[FF$61325]
    val validation: TaglessFreeCombinedInterpreterEx.Validation1.StackSafe[FF$61325]
    val loggingM: LoggingM[FF$61325]
  }

  object MMApp {
    type OpTypes = _root_.iota.TListK.Op.Concat[_root_.iota.TListK.Op.Concat[TaglessFreeCombinedInterpreterEx.Interaction1.StackSafe.OpTypes, TaglessFreeCombinedInterpreterEx.Validation1.StackSafe.OpTypes], LoggingM.OpTypes]
    type Op[AA$61330] = _root_.iota.CopK[OpTypes, AA$61330]

    class To[GG$61329[_]](implicit val interaction: TaglessFreeCombinedInterpreterEx.Interaction1.StackSafe[GG$61329], val validation: TaglessFreeCombinedInterpreterEx.Validation1.StackSafe[GG$61329], val loggingM: LoggingM[GG$61329]) extends MMApp[GG$61329] {}

    implicit def to[GG$61329[_]](implicit interaction: TaglessFreeCombinedInterpreterEx.Interaction1.StackSafe[GG$61329], validation: TaglessFreeCombinedInterpreterEx.Validation1.StackSafe[GG$61329], loggingM: LoggingM[GG$61329]): To[GG$61329] = new To[GG$61329]()

    def apply[FF$61325[_]](implicit ev$61331: MMApp[FF$61325]): MMApp[FF$61325] = ev$61331

    def instance(implicit ev: MMApp[Op]): MMApp[Op] = ev
  }

  TaglessFreeCombinedInterpreterEx.taglessProgram[MMApp.Op].interpret[Try]
}
