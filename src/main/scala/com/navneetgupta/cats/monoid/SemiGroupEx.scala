package com.navneetgupta.cats.monoid

/**
  * A semigroup is just the combine part of a monoid.
  * While many semigroups are also monoids, there are some data types for which we cannot define an empty element
  * Ex: non-empty sequences and posi􏰁ve integers, we are no longer able to define a sensible empty element.
  *
  * We can say like:
  *
  * trait Semigroup[A] {
  * def combine(x: A, y: A): A
  * }
  * trait Monoid[A] extends Semigroup[A] {
  * def empty: A
  * }
  *
  * If we define a Monoid for a type A, we get a Semigroup for free.
  * Similarly, if a method requires a parameter of type Semigroup[B], we can pass a Monoid[B] instead
  *
  */
object SemiGroupEx {

}
