package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._, Scalaz._

object SequenceEx extends  App {

  println(List(1.some, 2.some, 3.some).sequence)
  println(List(1.some, 2.some, none).sequence)
  

}
