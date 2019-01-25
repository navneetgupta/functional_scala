package com.navneetgupta.freestyle.effects

import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.effects.option._
import freestyle.free.effects.option.implicits._
import cats.implicits._
import cats.mtl.implicits._

object OptionEffectEx extends App {
//  option effect allows short circuiting of programs for optional values. It includes two basic operations: option and non
  def programNone[F[_]: OptionM] =
    for {
      a <- FreeS.pure(1)
      b <- OptionM[F].option[Int](None)
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programNone[OptionM.Op].interpret[Option])

  def programSome[F[_]: OptionM] =
    for {
      a <- FreeS.pure(1)
      b <- OptionM[F].option(Some(1))
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programSome[OptionM.Op].interpret[Option])

  def programNone2[F[_]: OptionM] =
    for {
      a <- FreeS.pure(1)
      b <- OptionM[F].none[Int]
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programNone2[OptionM.Op].interpret[Option])
}
