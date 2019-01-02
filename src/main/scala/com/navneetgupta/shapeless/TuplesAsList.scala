package com.navneetgupta.shapeless

import shapeless.syntax.std.tuple._

object TuplesAsList extends App {

  val a = (1, "foo", 12.3)

  println(a.tail) // tail . head normally not in scala, shapeless takes advantage of Hlist method by using tuples as Hlist
  println(a.head)
}
