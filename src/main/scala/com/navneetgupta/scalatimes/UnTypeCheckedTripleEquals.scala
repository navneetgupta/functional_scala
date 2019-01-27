package com.navneetgupta.scalatimes

import org.scalactic.TripleEquals._

object UnTypeCheckedTripleEquals extends App {
  // In ScalaTest use cases
  println(1L === 1) // Comparing Long with Int is wrong but will print true

  println(List(1,2,3) === Vector(1,2,3)) // Comparing List with Vector is wrong but will print true

  println("hi" === 1)  // should never compare

}
