package com.navneetgupta.scala.examples.tagged

import scalaz.Scalaz._
import scalaz._

object ProblemEx {

  // Tagged Types Or Value Classes

  // Simple
  final case class Student(firstName: String, lastName: String)

  //Suppose there are two different Student With name Navneet Gupta and Gupta Navneet
  val s1 = Student("Navneet", "Gupta")
  val s2 = Student("Gupta", "Navneet")

  implicit val StudentEqInstace = new Equal[Student] {
    def equal(s1: Student, s2: Student): Boolean = s1.firstName === s2.firstName && s1.lastName === s2.lastName
  }

  println(s1 != s2)

  // But if Someone entered by mistake lastname as firstName and viceversa. it can create havoc
  // Since there is nothing difference betwwen firstName /lastname both are string and Run time it will fail badly

  // To avoid raise the error during Compile time if someone try to exchange those value
}

object SolnUsingValueClassEx extends App {

  // Value classes:


  case class FirstName(firstName: String) extends AnyVal

  case class LastName(lastName: String) extends AnyVal

  final case class Student(firstName: FirstName, lastName: LastName)

  val s1 = Student(FirstName("Navneet"), LastName("Gupta"))
  val s2 = Student(FirstName("Gupta"), LastName("Navneet"))
  val s3 = Student(FirstName("Navneet"), LastName("Gupta"))

  //  object EqInstanceIncorrect {
  //    // Now the below Instance is not correct as s1.FirstName is never going to be equal until it refernce same thing
  //
  //    // The Below line will not compile Since we compiler will not find implicit instances for FirstName and LastName
  //
  //    implicit val StudentEqInstace = new Equal[Student] {
  //        def equal(s1: Student, s2 : Student) :Boolean = (s1.firstName === s2.firstName) && (s1.lastName === s2.lastName)
  //      }
  //
  //    def test() = {
  //      // With above implicit resolution
  //      println("---------Incorrect Implicit Defination => Below should be true if correct----------")
  //      println(s1 === s3 ) // This will fail even if we want it to be true
  //      println("---------Incorrect Implicit Defination-----------")
  //    }
  //  }


  object EqInstanceCorrectType1 {
    // Soln1
    // define Equal instance for FirstName and LastName also
    implicit val FirstNameEqInstance = new Equal[FirstName] {
      def equal(s1: FirstName, s2: FirstName): Boolean = s1.firstName === s2.firstName
    }

    implicit val LastNameEqInstance = new Equal[LastName] {
      def equal(s1: LastName, s2: LastName): Boolean = s1.lastName === s2.lastName
    }

    implicit val StudentEqInstance = new Equal[Student] {
      def equal(s1: Student, s2: Student): Boolean = s1.firstName === s2.firstName && s1.lastName === s2.lastName
    }

    def test() = {
      println("---------Correct Implict with FirstName/LastName implict -----------")
      println(s1 === s3) // This will fail even if we want it to be true
      println("---------Correct Implict with FirstName/LastName implict -----------")
    }
  }

  object EqInstanceCorrectType2 {
    // Soln1

    implicit val StudentEqInstance = new Equal[Student] {
      def equal(s1: Student, s2: Student): Boolean =
        s1.firstName.firstName == s2.firstName.firstName &&
          s1.lastName.lastName == s2.lastName.lastName
    }

    def test() = {
      println("---------Correct Implict with FirstName/LastName implict -----------")
      println(s1 === s3) // This will fail even if we want it to be true
      println("---------Correct Implict with FirstName/LastName implict -----------")
    }
  }

  EqInstanceCorrectType1.test()
  EqInstanceCorrectType2.test()

}

object IncorrectSolnUsingTypeAlias extends App {
  type FirstName = String
  type LastName = String

  final case class Student(firstName: FirstName, lastName: LastName)

  val s1 = Student("Navneet", "Gupta")
  val s2 = Student("Gupta", "Navneet")
  val s3 = Student("Navneet", "Gupta")

  // Type Alias extends the REadiness but does not help with the  solution . User can still mix up the things


}

object SolnUsingTaggedTypeParameters extends App {

  sealed trait FirstName

  sealed trait LastName

  type FirstNameType = String @@ FirstName
  type LastNameType = String @@ LastName

  def FirstName[A](a: A): A @@ FirstName = Tag[A, FirstName](a)

  def LastName[A](a: A): A @@ LastName = Tag[A, LastName](a)

  val firstName: FirstNameType = FirstName("Navneet")
  val lastName: LastNameType = LastName("Gupta")

  final case class Student(firstName: FirstNameType, lastName: LastNameType)

  val s1 = Student(FirstName("Navneet"), LastName("Gupta"))
  val s2 = Student(FirstName("Gupta"), LastName("Navneet"))
  val s3 = Student(FirstName("Navneet"), LastName("Gupta"))


  // To get Value from Tagged Type
  println(s"s1 FirstName : ${Tag.unwrap(s1.firstName)} , LastName : ${Tag.unwrap(s1.lastName)}")
  println(s"s2 FirstName : ${Tag.unwrap(s2.firstName)} , LastName : ${Tag.unwrap(s2.lastName)}")

  /**
    * exist only during compiler type - in runtime 'String @@ Name' degenerates into just 'String'
    * => no Performance issue while runtime
    **/

}


