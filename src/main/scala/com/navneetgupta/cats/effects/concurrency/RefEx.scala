package com.navneetgupta.cats.effects.concurrency

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import scala.concurrent.ExecutionContext

object RefEx extends App {
  /**
    * Provides safe concurrent access and modification of its content,
    * but no functionality for synchronisation, which is instead handled by Deferred.
    *
    * A Ref is always initialised to a value.
    *
    * The default implementation is nonblocking and lightweight,
    **/

  implicit val ctx = IO.contextShift(ExecutionContext.global)

  class Worker[F[_]](number: Int, ref: Ref[F, Int])(implicit F: Sync[F]) {

    private def putStrLn(value: String): F[Unit] = F.delay(println(value))

    def start: F[Unit] =
      for {
        c1 <- ref.get
        _ <- putStrLn(show"#$number >> $c1")
        c2 <- ref.modify(x => (x + 1, x))
        _ <- putStrLn(show"#$number >> $c2")
      } yield ()

  }

  val program: IO[Unit] =
    for {
      ref <- Ref.of[IO, Int](0)
      w1 = new Worker[IO](1, ref)
      w2 = new Worker[IO](2, ref)
      w3 = new Worker[IO](3, ref)
      _ <- List(
        w1.start,
        w2.start,
        w3.start
      ).parSequence.void
    } yield ()

  program.unsafeRunSync()

}
