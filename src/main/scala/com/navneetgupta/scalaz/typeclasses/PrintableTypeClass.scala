package com.navneetgupta.scalaz.typeclasses

trait Printable[A] {
  def format(a: A): String
}

object PrintableInstnces {
  implicit val intPrintableInstances: Printable[Int] = new Printable[Int] {
    override def format(a: Int): String = a.toString()
  }

  implicit val stringPrintableInstances: Printable[String] = new Printable[String] {
    override def format(a: String): String = a
  }

  implicit val orderPrintableInstances: Printable[Order] = new Printable[Order] {
    def format(a: Order): String = s"Order[quantity = ${a.quantity}, amount = ${a.amount}]"
  }

  implicit def optionInstances[A](implicit printable: Printable[A]): Printable[Option[A]] =
    new Printable[Option[A]] {
      def format(a: Option[A]): String = a match {
        case Some(x) => printable.format(x)
        case _       => ""
      }
    }
}

object PrintableApp extends App {
  import PrintableInstnces._

  type OptionalOrder = Option[Order]
  type OptionalInt = Option[Int]
  type OptionalString = Option[String]

  println(implicitly[Printable[Order]].format(Order(22.232, 43423.23)))
  println(implicitly[Printable[OptionalOrder]].format(Some(Order(22.232, 43423.23))))
  println(implicitly[Printable[OptionalOrder]].format(None))

  println(implicitly[Printable[Int]].format(1))
  println(implicitly[Printable[OptionalInt]].format(Some(1)))
  println(implicitly[Printable[OptionalInt]].format(None))

  println(implicitly[Printable[String]].format("Navneet gupta"))
  println(implicitly[Printable[OptionalString]].format(Some("Navneet Gupta")))
  println(implicitly[Printable[OptionalString]].format(None))
}

/**
 * Interface Methods for Printable
 */

object Printable {
  def toString[A](a: A)(implicit m: Printable[A]) = m.format(a)
}

object PrintableApp2 extends App {
  import PrintableInstnces._

  type OptionalOrder = Option[Order]
  type OptionalInt = Option[Int]
  type OptionalString = Option[String]

  println(Printable.toString(Order(22.232, 43423.23)))
  println(Printable.toString[OptionalOrder](Some(Order(22.232, 43423.23))))
  println(Printable.toString[OptionalOrder](None))

  println(Printable.toString(1))
  println(Printable.toString[OptionalInt](Some(1)))
  println(Printable.toString[OptionalInt](None))

  println(Printable.toString("Navneet Gupta"))
  println(Printable.toString[OptionalString](Some("Navneet Gupta")))
  println(Printable.toString[OptionalString](None))
}

/**
 * Interface Syntax
 */

object PrintbaleSyntax {
  implicit class printableSyntax[A](a: A) {
    def print(implicit p: Printable[A]): String = p.format(a)
  }
  implicit class optionPrintableSyntax[A](a: Option[A]) {
    def print(implicit p: Printable[A]): String = a match {
      case Some(x) => p.format(x)
      case _       => ""
    }
  }
}

object PrintableApp3 extends App {
  import PrintableInstnces._
  import PrintbaleSyntax._

  type OptionalOrder = Option[Order]
  type OptionalInt = Option[Int]
  type OptionalString = Option[String]

  println(Order(22.232, 43423.23).print)
  println(Some(Order(22.232, 43423.23)).print)
  val a: OptionalOrder = None
  println(a.print)

  println(1.print)
  println(Some(1).print)
  val b: OptionalInt = None
  println(b.print)

  println("Navneet Gupta".print)
  println(Some("Navneet Gupta").print)
  val c: OptionalString = None
  println(c.print)

}
