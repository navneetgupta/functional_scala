package com.navneetgupta.scalatimes

import org.scalactic.TypeCheckedTripleEquals._

object TypeCheckedTripleEqualsApp extends App {

  // below all line will generate compile time errors instead of giving wrong result
//  println(1L === 1) // Comparing Long with Int is wrong but will print true
//
//  println(List(1,2,3) === Vector(1,2,3)) // Comparing List with Vector is wrong but will print true
//
//  println("hi" === 1)  // should never compare

  println(1 === 1)

  println(List(1,2,3) === List(1,2,3))

  println("hi" === "hi")

  // If there is need to compare two types that are not compatible by default then there needs to be a conversion provided by you.
  // We can use a widening type ascription. A type ascription is simply a colon and a type placed next to a variable,
  // usually surrounded by parentheses. Since Seq[Int] is a common supertype of both Vector[Int] and List[Int],
  // the type constraint can be satisfied by widening either to their common supertype, Seq[Int]:
  println((List(1,2,3):Seq[Int]) === Vector(1,2,3))
  println(List(1,2,3) === (Vector(1,2,3):Seq[Int]))
}
