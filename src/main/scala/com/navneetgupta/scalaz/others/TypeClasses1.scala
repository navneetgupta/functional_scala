package com.navneetgupta.scalaz.others

object TypeClasses1 extends App {

  class User(val firstName: String, val lastName: String)

  def hello(user: User): String = "hi" + user

  println(hello(new User("Navneet", "Gupta"))) // hi  com.navneetgupta.scalaz.others.TypeClasses1$User@378bf509

  //-----------------------------------------------

  def hello2[A](a: A): String = "hi" + a

  println(hello2(new User("Navneet", "Gupta"))) // hicom.navneetgupta.scalaz.others.TypeClasses1$User@5fd0d5ae

  // ----------------------------------------------------
  // ----------------------------------------------------

  trait Show[A] {
    def show(a: A): String
  }

  val UserShow: Show[User] = new Show[User] {
    def show(user: User): String = user.firstName + " " + user.lastName
  }

  def hello3[A](a: A)(show: Show[A]): String = "Hi " + show.show(a)

  println(hello3(new User("Navneet", "Gupta"))(UserShow)) // Hi Navneet Gupta

  // ================================================================

  implicit val UserShow2: Show[User] = new Show[User] {
    def show(user: User): String = user.firstName + " " + user.lastName
  }
  def hello4[A](a: A)(implicit sh: Show[A]): String = "Hi " + sh.show(a)

  println(hello4(new User("Navneet", "Gupta"))) // Hi Navneet Gupta

  //==============================================================================
  //==============================================================================

  def hello5[A: Show](a: A): String = "Hi " + implicitly[Show[A]].show(a)

  println(hello5(new User("Navneet", "Gupta"))) // Hi Navneet Gupta

  //==============================================================================
  //==============================================================================
  //==============================================================================

  object Show {
    def apply[A: Show]: Show[A] = implicitly[Show[A]]
  }

  def hello6[A: Show](a: A): String = "Hi " + Show[A].show(a)

  println(hello6(new User("Navneet", "Gupta"))) // Hi Navneet Gupta

  //###############################################################################

  implicit class ShowOps[A: Show](a: A) {
    def show: String = Show[A].show(a)
  }

  def hello7[A: Show](a: A): String = "Hi " + a.show // new ShowOps(a).show

  println(hello7(new User("Navneet", "Gupta"))) // Hi Navneet Gupta

  //###############################################################################
  //###############################################################################

}
