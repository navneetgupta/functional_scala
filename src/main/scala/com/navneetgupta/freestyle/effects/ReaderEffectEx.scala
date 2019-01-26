package com.navneetgupta.freestyle.effects

import cats.data.Reader
import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.effects.reader

object ReaderEffectEx extends App {
  // reader effect allows obtaining values from the environment.
  // The initial seed for the environment value is provided at runtime interpretation.
  // reader effect supports parametrization to any seed value type while remaining type safe throughout the program declaration.
  //needs to be implicit evidence of cats.mtl.ApplicativeAsk[M, R] and Monad[M] for any runtime M[_] used in its interpretation due to the constraints placed by this effect.
  // reader effect comes with two operations ask and reader.

  case class Config(n: Int)
  type ConfigEnv[A] = Reader[Config, A]
  val rd = reader[Config]

  import cats.mtl.implicits._
  import rd.implicits._

  def programAsk[F[_]: rd.ReaderM] =
    for {
      _ <- FreeS.pure(1)
      c <- rd.ReaderM[F].ask
      _ <- FreeS.pure(1)
    } yield c

  println(programAsk[rd.ReaderM.Op].interpret[ConfigEnv].run(Config(n = 10)))


  // reader allows extracting values of the environment and lifting them into the context of FreeS

  def programReader[F[_]: rd.ReaderM] =
    for {
      a <- FreeS.pure(1)
      b <- rd.ReaderM[F].reader(_.n)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programReader[rd.ReaderM.Op].interpret[ConfigEnv].run(Config(n = 1)))
}
