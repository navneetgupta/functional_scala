package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._,Scalaz._

object StreamEx extends App {
  val streamList = unfold(10) {(x) => if(x == 0 ) none else (x, x-1).some}

  println(streamList.toList)

  def minimumStream[A: Order](strm : Stream[A]) = strm match {
    case x #:: xs => xs.foldLeft(x) (_ min _)
  }
  def maximumStream[A: Order](strm : Stream[A]) = strm match {
    case x #:: xs => xs.foldLeft(x) (_ max _)
  }

  println(minimumStream(streamList))
  println(maximumStream(streamList))

  def deleteS[A: Equal](stream: Stream[A], y: A) : Stream[A] = (stream, y) match {
    case (Stream(), _) => Stream()
    case (x1 #:: xs, y1 ) =>
      if(y1 == x1) xs
      else x1 #:: deleteS(xs, y)
  }

  println(deleteS(streamList, 4).toList)

  def deleteMin[A: Order](stream: Stream[A]): Option[(A, Stream[A])] = stream match  {
    case Stream() => none
    case xs =>
      val min = minimumStream(xs)
      (min, deleteS(xs, min)).some
  }

  def ssort[A: Order](stream: Stream[A]): Stream[A] = unfold(stream)(deleteMin[A])


  println(deleteMin(streamList).toList)
  println(ssort(streamList).toList)


}
