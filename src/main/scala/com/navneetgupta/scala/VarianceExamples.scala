package com.navneetgupta.scala


object VarianceExamples {
  // Wikipedia :=> Variance refers to how subtyping between more complex types relates to subtyping between their components

  // Types: Every Value(result of any expresssion) has a type. A type has finite/Infinite Possible Values
  // Boolean Type has only Two Possible Values: True/ False
  //

  // Sum Type: Person (Sum Type) has four possible Values (Dollar/ Pound / Euro / Rupees )
  sealed trait Currency
  case object Dollar extends Currency
  case object Pound extends Currency
  case object Euro extends Currency
  case object Rupees extends Currency

  // Product Type: Combination of n values
  case class DeptUnit(name: String, id: String)

  // SubTypes:
  // Employee/ Manager are subType of Person
  // Some/None are subTypes of Option

  sealed trait Person
  case class Employee(name: String, empId: Int) extends Person
  case class Manager(name: String, empId: Int) extends Person


  // Kinds:

  // Person has as kind (*) as it takes no type Paramers
  // Option[A] has a kind of * -> * as it take one type parameters



  /**
    * Check Type of any expression => scala> :t expression
    * Check Type of a function `fun`   => scala> :t fun
    *
    * Check Kind of a Type `type` => scala> :k type
    *
    * :k -v Person => A => This is a proper type.+ Its not a type Constructor so donot accept the Type parameters =>  its Kind is  *
    * :k -v Option => F[+A] => This is a type constructor, This says: given one type, produce another: a 1st-order-kinded type.=> its Kind is * -(+)-> *
    * :k -v Map => F[A1, +A2] => This is a type constructor, This says: given one type, then another, produce the final type.: a 1st-order-kinded type. => Its Kind is * -> * -(+)-> *
    * :k -v Seq => F[+A] => This is a type constructor, This says: given one type, produce another: a 1st-order-kinded type.=> its Kind is * -(+)-> *
    * :k -v List => F[+A] => This is a type constructor, This says: given one type, produce another: a 1st-order-kinded type.=> its Kind is * -(+)-> *
    * :k -v Range => A => This is a proper type + Its not a type Constructor so donot accept the Type parameters => its Kind is *
    *
    *
    *
    * scala> sealed trait Functor[F[_]]
    *
    * :k -v Functor => This is a type constructor that takes type constructor(s): a higher-kinded type. => Its Kind is (* -> *) -> *
    *
    * For More Details Refer to Image : images/higher_kinded_types.jpg in the image folder at base of project
    *
    * */

    // Variance
}

object Model {
  abstract class Org {
    def address: String
  }
  case class HeadBranch(address: String) extends Org
  case class RegionalBranch(address: String) extends Org
}

object CovarianceTestApp extends App {
  /**
    * CoVariance:
    *
    * A type parameter A of a generic class can be made covariant by using the annotation +A.
    * For some class List[+A], making A covariant implies that for two types A and B where A is a subtype of B,
    * then List[A] is a subtype of List[B]. This allows us to make very useful and intuitive subtyping
    * relationships using generics.
    * */

  import Model._
  def printOrgAddresses(orgs: List[Org]) = {
    orgs.foreach(x => println(x.address))
  }

  printOrgAddresses(List(HeadBranch("address1"), HeadBranch("address 2"), HeadBranch("address 3")))
  printOrgAddresses(List(RegionalBranch("regional address1"), RegionalBranch("regional address 2"), RegionalBranch("regional address 3")))

  // Above the Method Accepts List[Org] but we are calling method on SubType of Org
  // and its works because List is Covarrinat in its Type List[+A].
}

object ContraVarainceTestApp extends App {
  /**
    * Contravariance::
    *
    * A type parameter A of a generic class can be made contravariant by using the annotation -A
    * some class Writer[-A], making A contravariant implies that for two types A and B where A is
    * a subtype of B, Writer[B] is a subtype of Writer[A].
    *
    * */
  import Model._

  abstract class Print[-A] {
    def printIt(a: A): Unit
  }

  class HeadBranchPrinter extends Print[HeadBranch] {
    override def printIt(a: HeadBranch): Unit = println(s"The Head Branch address is ${a.address}")
  }
  class OrgPrinter extends Print[Org] {
    override def printIt(a: Org): Unit = println(s"The Org address is ${a.address}")
  }

  val headBranch = HeadBranch("Address 1")

  def printHeadBranch(print: Print[HeadBranch]): Unit = {
    print.printIt(headBranch)
  }

  printHeadBranch(new HeadBranchPrinter)
  printHeadBranch(new OrgPrinter)

  // The PrintHEadBranch takes the Print[HeadBranch] but it will also work with SuperType
  // Print[Org] since it is Covariant in Print[-A]


  // Just remove Covariant type(-A) and replace with InVarinat A and it will not work static error




}

// Lets See with List

object InVariantTypeExWithList {
  //

  abstract class List[T] { }
  case object Nil extends List[Nothing]
  case class Cons[T](head: T, tail: List[T]) extends List[T]

  // The below fail to Compile because List is Invarinat to type T(Org in Below line)
  // and since its invariant => it only takes T not sub/susper Type of T

//  val list: List[Org] = Cons(Model.HeadBranch("address1"), Cons(HeadBranch("address2"), Nil))
}

object CoVariantTypeExWithList {
  //
  import Model._

  abstract class List[+T] { }
  case object Nil extends List[Nothing]
  case class Cons[+T](head: T, tail: List[T]) extends List[T]

  // The below fail to Compile because List is Invarinat to type T(Org in Below line)
  // and since its invariant => it only takes T not sub/susper Type of T

  val list: List[Org] = Cons(HeadBranch("address1"), Cons(HeadBranch("address2"), Nil))
}

object ContraVaraintTypeExWithList {

//  abstract class List[-T] { }
//  case object Nil extends List[Nothing]
//  case class Cons[-T](head: T, tail: List[T]) extends List[T]
//
//  abstract class School extends Org {}
//
//  class DAV extends School {
//    override def address: String = "D.A.V."
//  }
//
//  class STU extends School {
//    override def address: String = "Stanford College"
//  }

  // The below fail to Compile because List is Invarinat to type T(Org in Below line)
  // and since its invariant => it only takes T not sub/susper Type of T

//  val list: List[DAV] = Cons(HeadBranch("address1"), Cons(HeadBranch("address2"), Nil))
}

