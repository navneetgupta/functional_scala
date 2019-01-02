package com.navneetgupta.shapeless

import shapeless.HList

object FunctionWithArbitraryArity extends App {
  class FunctionClass[H <: HList](hs: H)

//  object FunctionClass {
//    def apply[A](a: A) = new FunctionClass(a :: HNil)
//    def apply[A, B](a: A, b: B) = new FunctionClass(a :: b :: HNil)
//    def apply[A, B, C](a: A, b: B, c: C) = new FunctionClass(a :: b :: c :: HNil)
//    // etc Different Arity apply Function Takes advantage of Hlist Invariant types
//  }


  // Above Could be annoying to write apply for different number of arity.
  // Which can be solved as below

  object FunctionClass {
    import shapeless.Generic
    def apply[P <: Product, L <: HList](p: P)(implicit gen: Generic.Aux[P, L]) =
      new FunctionClass[L](gen.to(p))
  }

  FunctionClass(1, "Hello") // FunctionClass[Int :: String :: HNil]
  FunctionClass(1, "Hello", 12.6) // FunctionClass[Int :: String :: Double :: HNil]


}
