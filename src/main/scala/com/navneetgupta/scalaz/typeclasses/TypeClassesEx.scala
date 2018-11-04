package com.navneetgupta.scalaz.typeclasses

/**
  * TypeClasses are basically a pattern that allow us to extend existing libraries with additional functionality,
  * without using traditional inheritance, and without altering the original library source code
  */

/**
  * Important Components/Parts of TypeClass are:
  * 1.   the type class itself
  * 2.   instances for particular types, and
  * 3.   the interface methods that we expose to users.
  */

object TypeClassesEx {

}

/**
  * A type class is an interface or API that represents some functionality we want to implement.
  * Suppose I want to expose Some Algebra(For Ex Plus/addition) on other types
  *
  * See the below type class
  */

trait Algebra[A] {
  def plus(a: A, b: A): A
}

/**
  * writting Plus instances for order
  */

object AlgebraInstances {
  implicit val orderInstances: Algebra[Order] = new Algebra[Order] {
    override def plus(a: Order, b: Order): Order = Order(a.quantity + b.quantity, a.amount + b.amount)
  }

  implicit def optionAlgebra[A](implicit algebra: Algebra[A]): Algebra[Option[A]] =
    new Algebra[Option[A]] {
      def plus(a: Option[A], b: Option[A]): Option[A] = (a, b) match {
        case (Some(x), Some(y)) => Some(algebra.plus(x, y))
        case _ => None
      }
    }
}

/**
  * Writting The Interface Methods For User
  */
object Algebra {
  def add[A](a: A, b: A)(implicit p: Algebra[A]): A = p.plus(a, b)
}

/**
  * Type Class Instances.
  *
  * Suppose i want to Add the orders:
  * Where Order is represented as below
  */

case class Order(quantity: Double, amount: Double)

object TestApp extends App {

  import AlgebraInstances._
  import Algebra._

  val o1 = Order(10.2, 2345.2323)
  val o2 = Order(12.11, 43223.2323)
  println(Algebra.add(o1, o2))
}

/**
  * Now above Algebra type class can be extend to more functionality Divide,Subtract,Multiply etc.
  */

/**
  * Even though we have written a instances for Order and using
  * Algebra.add it would be better if we could directly do o1.add(o2)
  */

/**
  * Inteface MEthods Or Interface Syntax
  */

object AlgebraSyntax {

  implicit class algebraSyntax[A](a: A) {
    def add(b: A)(implicit p: Algebra[A]): A = p.plus(a, b)
  }

}

object TestApp2 extends App {

  import AlgebraSyntax._
  import AlgebraInstances._

  val o1 = Order(10.2, 2345.2323)
  val o2 = Order(12.11, 43223.2323)

  println(o1.add(o2))

}

///**
// * Scala Standard Library also provides a generic type class interface called implicitly
// */
//
object TestApp3 extends App {

  import AlgebraInstances._

  // Instead of Using ImplicitSyntax or InterfaceMethods We can Use Scala standard library implicitly
  println(implicitly[Algebra[Order]].plus(Order(10.2, 2345.2323), Order(12.11, 43223.2323)))
}

/**
  * Packaging Implicits
  *
  * 1. by placing them in an object such as AlgebraInstances;
  * 2. by placing them in a trait;
  * 3. by placing them in the companion object of the type class;
  * 4. by placing them in the companion object of the parameter type.
  *
  */

/**
  * Above we are defining concrete instances as implicit vals of the required type.
  * like For Order whihc is a Concrete Type.
  *
  * But What if we have to define implict for Option[Order] / Order[A]
  *
  * For defining implicit for Order[A] we have to define implicit value for 'A' as well as for Option[A] and **for each type of A**
  *
  * So for Ex defining for Option[A]
  *
  * implicit val optionOrderAlgebra : Algebra[Option[Order]] = ???
  * Plus we have to define for A also
  * implicit val orderInstances: Algebra[Order] = ???
  *
  * Similalry for Type Int.
  * implicit val optionIntAlgebra : Algebra[Option[Int]] = ???
  * implicit val intInstances: Algebra[Int] = ???
  *
  * Similalry for Type String.
  * implicit val optionStringAlgebra : Algebra[Option[String]] = ???
  * implicit val stringInstances: Algebra[String] = ???
  *
  *
  * The above is clearly not scalable since we have to define seprately for each type.
  *
  * Can we write a single instance for Each Type(Int, String, ORder .....)?
  */

/**
  * But Instead of defining Concrete implicit instances we can define implicit methods to construct instances from other type class instances.
  *
  * See As Below.
  */

object TestApp4 extends App {

  import AlgebraInstances._

  // We can use implicitly ore define ImplicitSyntax or InterfaceMethods as above
  val l = implicitly[Algebra[Option[Order]]].plus(Some(Order(10.2, 2345.2323)), Some(Order(12.11, 43223.2323)))
  println(l)
}
