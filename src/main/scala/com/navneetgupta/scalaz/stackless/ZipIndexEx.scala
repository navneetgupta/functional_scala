package com.navneetgupta.scalaz.stackless

import scalaz.State

object ZipIndexEx {
  def zipIndex[A](xs: List[A]): List[(Int,A)] =
    xs.foldLeft((Nil:List[(Int,A)], 0))((b, a) => ((b._2,a)::b._1, b._2+1))._1.reverse
}


object ZipIndexExApp extends App {

  println(ZipIndexEx.zipIndex(List(1,2,3,4,5,6,7,8,9,10)))

  (ZipIndexEx.zipIndex((1 to 10000000).toList)) // StackOverFlow
}

object ZipIndexTrampolinedEx {

  def pureState[A] = State[Int, List[(Int,A)]](List())


//  pureState.

//  def zipIndex[A](as: Seq[A]): State[Int, List[(Int, A)]] =
//    as.foldLeft(State[Int, List[(Int, A)]](List()))((acc, a) => for {
//      xs <- acc
//      n <- getState
//      _ <- setState(n + 1)
//    } yield (n, a) :: xs)
}