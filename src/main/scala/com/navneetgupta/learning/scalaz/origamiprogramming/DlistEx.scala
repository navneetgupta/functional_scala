package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._,Scalaz._


object DlistEx extends App {

  // DList is a datssturcture that support constant time appending which is differnece list

  val dlist = DList.unfoldr(10, { (x: Int) => if (x == 0) none else (x, x - 1).some })

  println(dlist.toList)
}


