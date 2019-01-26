package com.navneetgupta.freestyle.effects

import cats.data.Writer
import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.effects.writer


object WriterEffectEx extends App {
  //writer effect allows for the accumulation of values, which can be obtained once the program is interpreted.
  // writer effect supports parametrization to any type that supports monoidal accumulation while remaining type safe throughout the program declaration.
  //needs to be implicit evidence of cats.mtl.ApplicativeAsk[M, W] and Monad[M] for any runtime M[_] used in its interpretation due to the constraints placed by this effect.
  //writer effect comes with two operations writer and tell.


  import cats.mtl.implicits._
  import cats.implicits._

  val wr = writer[List[Int]]
  import wr.implicits._

  type Logger[A] = Writer[List[Int], A]

  def programWriter[F[_]: wr.WriterM] =
    for {
      _ <- FreeS.pure(1)
      b <- wr.WriterM[F].writer((Nil, 1))
      _ <- FreeS.pure(1)
    } yield b

  println(programWriter[wr.WriterM.Op].interpret[Logger].run)

  def programTell[F[_]: wr.WriterM] =
    for {
      _ <- FreeS.pure(1)
      b <- wr.WriterM[F].writer((List(1), 1))
      c <- wr.WriterM[F].tell(List(1))
      _ <- FreeS.pure(1)
    } yield b

  println(programTell[wr.WriterM.Op].interpret[Logger].run)
}
