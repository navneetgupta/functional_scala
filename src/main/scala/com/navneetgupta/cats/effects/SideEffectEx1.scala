package com.navneetgupta.cats.effects

object SideEffectEx1 extends App {

  println("-------------------")
  val expr = println("Hi")
  println((expr, expr))
  println("-------------------")

  println("======================")
  // Substituting expr with its value
  println((println("Hi"), println("Hi")))
  println("======================")


  /**
    * Output
    *
    * -------------------
    * Hi
    * ((),())
    * -------------------
    * ======================
    * Hi
    * Hi
    * ((),())
    * ======================
    *
    **/

  // Clearly Substituting value does not result in same output.. => it has side effect.
  //

}
