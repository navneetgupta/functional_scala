package com.navneetgupta.cats.show

/**
 *  variance relates to subtypes. We say that B is a sub- type of A if we can use a value of type B anywhere we expect a value of type A.
 *   Co- and contravariance annota􏰁tions arise when working with type constructors.
 *
 * Type Constructor like Option, Future, List, Set etc. whihc take type parameter for a valid type
 *
 *     trait F[+A] // the "+" means "covariant"
 *       Covariance means that the type F[B] is a subtype of the type F[A] if B is a subtype of A
 *       The covariance of Scala collec􏰁ons allows us to subs􏰁tute collec􏰁ons of one type for another in our code
 *       For example, we can use a List[Circle] any- where we expect a List[Shape] because Circle is a subtype of Shape:
 *     trait F[-A]
 *       Confusingly, contravariance means that the type F[B] is a subtype of F[A] if A is a subtype of B
 *
 *       trait JsonWriter[-A] {
 *           def write(value: A): Json
 *       }
 *
 *     Invariance
 *         Invariance is actually the easiest situa􏰁on to describe. It’s what we get when we don’t write a + or - in a type constructor:
 *         trait F[A]
 *
 *
 * When the compiler searches for an implicit it looks for one matching the type or subtype.
 * Thus we can use variance annota􏰁ons to control type class instance selec􏰁on to some extent.
 */

sealed trait Animal
object VarianceShow {

}
