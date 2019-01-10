package com.navneetgupta.cats.effects.concurrency

import cats.effect.IO
import cats.effect.concurrent.Deferred
import cats.implicits._
import scala.concurrent.ExecutionContext

object DefferedEx extends App {
  /**
    * A purely functional synchronization primitive which represents a single value which may not yet be available.
    *
    * When created, a Deferred is empty. It can then be completed exactly once, and never be made empty again.
    *
    *
    * Expected behavior of get
    *
    * get on an empty Deferred will block until the Deferred is completed
    * get on a completed Deferred will always immediately return its content
    * get is cancelable if F[_] implements Concurrent and if the Deferred value was built via the normal apply
    * (and not via uncancelable); and on cancellation it will unsubscribe the registered listener, an operation that’s possible for as long as the Deferred value isn’t complete
    *
    * Expected behavior of complete
    *
    * complete(a) on an empty Deferred will set it to a, and notify any and all readers currently blocked on a call to get.
    * complete(a) on a Deferred that has already been completed will not modify its content, and result in a failed F.
    *
    *
    * The restriction on the uncancelable builder is just Async, whereas the restriction on the normal apply builder is Concurrent.
    * */

  // When u need who wins the race will be first taken
  // The loser one will raise an error when trying to complete a deferred already completed and
  // automatically be canceled by the IO.race mechanism, that’s why we call attempt on the evaluation.

  implicit val cs = IO.contextShift(ExecutionContext.global)

  // attemp on IO is analogous to the catch clause in try/catch, being the inverse of IO.raiseError. Example:
  // boom.attempt.unsafeRunSync() // boom raises error val boom = IO.raiseError(new Exception("boom"))
  // res49: Either[Throwable,Nothing] = Left(java.lang.Exception: boom)
  def start(d: Deferred[IO, Int]): IO[Unit] = {
    val attemptCompletion: Int => IO[Unit] = n => d.complete(n).attempt.void // if not attempted it will raise error already completed

    List(
      IO.race(attemptCompletion(1), attemptCompletion(2)),
      d.get.flatMap { n => IO(println(show"Result: $n")) }
    ).parSequence.void
  }

  val program: IO[Unit] =
    for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()

  program.unsafeRunSync()
}
