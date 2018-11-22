package com.navneetgupta.learning.scalaz.lawless

import scalaz._, Scalaz._

object LawlessTypeclassesEx extends App {

  //Length


  // Index

  // noraml scala
//  println(List(1,2,3)(3)) // java.lang.IndexOutOfBoundsException: 3
  println(List(1,2,3) index 3) // None


  
}
