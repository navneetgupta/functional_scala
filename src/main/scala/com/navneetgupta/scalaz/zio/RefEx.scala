package com.navneetgupta.scalaz.zio

import scalaz.zio.{Ref, IO}
import scalaz.zio.console._

object RefEx extends App {
  // Ref[A] models a mutable reference to a value of type A
  // two basic operations are `set`, which fills the Ref with a new value, and `get to retrieve
  //  All operations on a Ref are atomic and thread-safe, providing a reliable foundation for synchronizing concurrent programs.

  for {
    ref <- Ref(100)
    v1 <- ref.get
    v2 <- ref.set(v1 - 50)
  } yield v2

  // Updating
  def repeat[E, A](n: Int)(io: IO[E, A]): IO[E, Unit] =
    Ref(0).flatMap { iRef =>
      def loop: IO[E, Unit] = iRef.get.flatMap { i =>
        if (i < n)
          io *> iRef.update(_ + 1) *> loop
        else
          IO.unit
      }

      loop
    }


}

object RefEx2 extends App {
  def main2() = {
    for {
      ref <- Ref(100)
      v1 <- ref.get
      v2 <- ref.set(v1 - 50)
      _ <- putStrLn("Value v1 = " + v1)
      _ <- putStrLn("Value v2 = " + v2)
    } yield ()
  }

  main2().forever
}
