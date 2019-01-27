package com.navneetgupta.freestyle.effects

import freestyle.free.FreeS
import freestyle.free._
import freestyle.free.effects.traverse
import freestyle.free.implicits._
import cats.implicits._

object TraverseEffectEx extends App {
  // traverse effect
  // acts as a generator and works with any G[_] for which a cats.Foldable instance is available.
  // The target runtime M[_] requires a Monad[M] and an Alternative[M] instance.
  // Traverse includes two basic operations fromTraversable and empty.

  val list = traverse[List]

  import list._, list.implicits._
  import list._
  import list.implicits._

  def programTraverse[F[_]: TraverseM] =
    for {
      a <- TraverseM[F].fromTraversable(1 :: 2 :: 3 :: Nil) //fromTraversable allows the lifting of any G[_]: Foldable into the context of FreeS:
      b <- (a + 1).pure[FreeS[F, ?]]
    } yield b

  println(programTraverse[TraverseM.Op].interpret[List])

  // empty allows the short circuiting of programs providing the empty value for the G[_] through the final MonadCombine.
  // In the same way as OptionM#none, the empty value is determined by how the MonadCombine instance for the final M[_]
  // is implemented.

  def programEmpty[F[_]: TraverseM] =
    for {
      _ <- TraverseM[F].empty[Int]
      a <- TraverseM[F].fromTraversable(1 :: 2 :: 3 :: Nil)
      b <- FreeS.pure(a + 1)
      c <- FreeS.pure(b + 1)
    } yield c

  println(programEmpty[TraverseM.Op].interpret[List])
}
