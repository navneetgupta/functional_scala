package com.navneetgupta.scalaz.IO

import scalaz.effect.IO._
import scalaz.effect._
import scalaz._, Scalaz._

object IOMonadEx extends App {
  /**
    * While ST gives us guarantees that mutable memory is never shared, it says nothing about reading/writing files,
    * throwing exceptions, opening network sockets, database connections, etc.
    *
    *
    * typeclass contract of ST:
    *
    * sealed trait ST[S, A] {
    *   private[effect] def apply(s: World[S]): (World[S], A)
    * }
    *
    * And the following is the typeclass contract of IO:
    *
    * sealed trait IO[+A] {
    *   private[effect] def apply(rw: World[RealWorld]): Trampoline[(World[RealWorld], A)]
    * }
    * */

  val action1 = for {
    _ <- putStrLn("Hello, world!")
  } yield ()

  println(action1.unsafePerformIO)


  val action2 = IO {
    val source = scala.io.Source.fromFile("./build.sbt")
    source.getLines.toStream
  }

  println(action2.unsafePerformIO.toList)


  // Composing IO is done Monadically

  def program: IO[Unit] = for {
    line  <- readLn
    _     <- putStrLn(line)
  } yield ()

  //  IO[Unit] is an instance of Monoid, so we can re-use the monoid addition function |+|.
  println((program |+| program).unsafePerformIO)
}
