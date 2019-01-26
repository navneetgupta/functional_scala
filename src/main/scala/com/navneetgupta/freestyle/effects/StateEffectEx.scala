package com.navneetgupta.freestyle.effects

import cats.data.State
import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.effects.state

object StateEffectEx extends App {
  // state effect enables purely functional state throughout programs.
  //state effect supports parametrization to any type remaining type safe throughout the program declaration.
  // needs to be implicit evidence of cats.mtl.MonadState[M, S] and Monad[M] for any runtime M[_] where S is the type of state due to the constraints placed by this effect.
  //  four basic operations get, set, modify, and inspect.

  import cats.implicits._
  import cats.mtl.implicits._

  val st = state[Int]
  type TargetState[A] = State[Int, A]

  import st.implicits._

  def programGet[F[_]: st.StateM] =
    for {
      a <- FreeS.pure(1)
      b <- st.StateM[F].get
      c <- FreeS.pure(1)
    } yield a + b + c

  println(programGet[st.StateM.Op].interpret[TargetState].run(6).value)

  // set replaces the current state:
  def programSet[F[_]: st.StateM] =
    for {
      _ <- st.StateM[F].set(1)
      a <- st.StateM[F].get
    } yield a

  println(programSet[st.StateM.Op].interpret[TargetState].run(0).value)

//  modify modifies the current state:
  def programModify[F[_]: st.StateM] =
    for {
      a <- st.StateM[F].get
      _ <- st.StateM[F].modify(_ + a)
      b <- st.StateM[F].get
    } yield b

  println(programModify[st.StateM.Op].interpret[TargetState].run(1).value)

  //inspect runs a function over the current state and returns the resulting value: does not modify
  def programInspect[F[_]: st.StateM] =
    for {
      a <- st.StateM[F].get
      b <- st.StateM[F].inspect(_ + a)
    } yield b

  println(programInspect[st.StateM.Op].interpret[TargetState].run(1).value)
}
