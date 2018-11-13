package com.navneetgupta.learning.scalaz.monad

import scalaz._

object StateMonadEx extends App {
  /**
    * Haskell features a thing called the state monad, which makes dealing with stateful problems a breeze while still keeping everything nice and pure.
    *
    * We’ll say that a stateful computation is a function that takes some state and returns a value along with some new state.
    * That function would have the following:
    * S => (A, S)
    *
    *
    * type State[S, +A] = StateT[Id, S, A]
    *
    * // important to define here, rather than at the top-level, to avoid Scala 2.9.2 bug
    * object State extends StateFunctions {
    * def apply[S, A](f: S => (S, A)): State[S, A] = new StateT[Id, S, A] {
    * def apply(s: S) = f(s) }
    * }
    *
    * trait StateT[F[+_], S, +A] { self =>
    *
    * /** Run and return the final value and state in the context of `F` */
    *    def apply(initial: S): F[(S, A)]
    *
    *    /** An alias for `apply` */
    *    def run(initial: S): F[(S, A)] = apply(initial)
    *
    *    /** Calls `run` using `Monoid[S].zero` as the initial state */
    *    def runZero(implicit S: Monoid[S]): F[(S, A)] = run(S.zero)
    * }* */

  type Stack = List[Int]

  val pop =  State[Stack, Int] {
    case x :: xs => (xs, x)
    case a => (Nil, 0)
  }

  def push(a: Int) = State[Stack, Unit] {
    case xs => (a :: xs,
      (
  )
    )
  }

  def stackManip = for {
    _ <- push(3)
    a <- pop
    b <- pop
  } yield (b)

  println(stackManip(List(1, 2, 3)))

}
