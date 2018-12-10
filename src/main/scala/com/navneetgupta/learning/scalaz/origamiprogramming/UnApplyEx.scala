package com.navneetgupta.learning.scalaz.origamiprogramming


import scalaz._, Scalaz._

object UnApplyEx extends App {

  /**
    *  an instance of Applicative[M[_]] is (* -> *) -> * (a type constructor that takes another type constructor that takes exactly one type).
    *  It’s known that Int => Int can be treated as an applicative by treating it as Int => A:
    * */


  Applicative[Function1[Int, Int]]

  Applicative[({type l[A]=Function1[Int, A]})#l]

  // This becomes annoying for M[_,_] like Validation. One of the way Scalaz helps you out is to provide meta-instances of typeclass instance called Unapply.

  


}
