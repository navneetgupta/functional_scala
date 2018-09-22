package com.navneetgupta.scalaz.polymorphism

object EqualityCheck extends App {
  println("1" == 1) // This is the probelm this even should not compile since wea re trying to compare String to Int

  /**
   * To Improve this One Scalaz provide
   *
   * trait Equal[A] {
   *     def equal(a1: A, a2: A): Boolean
   * }
   * Which Describe and Put a Premise on Types that it should be a same type
   */
  //
  import scalaz._
  import Scalaz._

  //println("1" === 1) // Now here if we note it clearly says Type Mismatch and provides us with compile time error
}
