package com.navneetgupta.scalaz.others

import scalaz.Scalaz._
import scalaz._

object Exercise extends App {

  // Noraml Merge
  def merge(m1: Map[String, Int], m2: Map[String, Int]): Map[String, Int] = {
    val keys = m1.keys ++ m2.keys
    val kys = keys.map {
      k => (k -> (m1.getOrElse(k, 0) + m2.getOrElse(k, 0)))
    }
    Map[String, Int](kys.toSeq: _*)
  }

  // Abstracted Merge
  def merge2[A, B: Monoid](
                            m1: Map[A, B],
                            m2: Map[A, B]): Map[A, B] = {
    val keys = m1.keys ++ m2.keys
    val zero = Monoid[B].zero

    val kys = keys.map {
      k => (k -> (m1.getOrElse(k, zero) |+| m2.getOrElse(k, zero)))
    }
    Map[A, B](kys.toSeq: _*)
  }

}
