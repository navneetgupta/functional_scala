package com.navneetgupta.scala

import com.navneetgupta.scala.Model.Org

object UpperTypeBoundEx extends App {

  abstract class School extends Org {}

  class DAV extends School {
    override def address: String = "D.A.V."
  }

  class STU extends School {
    override def address: String = "Stanford College"
  }

  class SchoolContainer[P <: School](p: P) {
    def school: School = p
  }

  val davSchool = new SchoolContainer(new DAV)
  println(davSchool.school.address)

  val stanfordSchool = new SchoolContainer(new STU)
  println(stanfordSchool.school.address)


  // This will Fail Since P is upperbounded to School and HEadBranch is not an Org.
  //  val headBranch = new SchoolContainer(new HeadBranch("Address 1"))
  //  println(headBranch.school.address)


}

