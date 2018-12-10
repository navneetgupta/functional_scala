package com.navneetgupta.learning.scalaz.origamiprogramming


import scalaz._ , Scalaz._

object ComposeEx extends App {
  val f = (_ : Int) + 1
  val g = ( _ : Int) * 100

  println(g(f(2)))
  println((f >>> g)(2))  // Compose Syntax  defined in ComposeOps trait


  println(f(g(2)))
  println((f <<< g)(2))

}
