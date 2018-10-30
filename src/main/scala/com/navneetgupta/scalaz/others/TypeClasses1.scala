package com.navneetgupta.scalaz.others

object TypeClasses1 extends App {
  trait Show[A] {
    def show(a: A): String
  }

  object Show {
    def apply[A: Show]: Show[A] = implicitly[Show[A]]

    implicit class ShowOps[A: Show](a: A) {
      def show: String = a.show
    }
  }

  class User(val firstName: String, val lastName: String)

  def hello[A: Show](a: A): String = {
    import Show._
    "hi " + a.show
  }

  // Type classes type safety Javascript equals compariring String to int.
}
