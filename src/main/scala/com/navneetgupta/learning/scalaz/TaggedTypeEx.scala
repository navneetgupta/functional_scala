package com.navneetgupta.learning.scalaz

import scalaz.Scalaz._
import scalaz._

object TaggedTypeEx extends App {

  // Tagged type

  // type Tagged[U] = { type Tag = U }
  // type @@[T, U] = T with Tagged[U]

  val mass = KiloGram(20.0)

  def KiloGram[A](a: A): A @@ KiloGram = Tag[A, KiloGram](a)

  def energyR(m: Double @@ KiloGram): Double @@ JoulePerKiloGram = JoulePerKiloGram(299792458.0 * 299792458.0 * Tag.unsubst[Double, Id, KiloGram](m))

  println(2 * Tag.unwrap(mass))

  println((2 * scalaz.Tag.unsubst[Double, Id, KiloGram](mass)))

  // Just to be clear, A @@ KiloGram is an infix notation of scalaz.@@[A, KiloGram]. We can now define a function that calculates relativistic energy.

  def JoulePerKiloGram[A](a: A): A @@ JoulePerKiloGram = Tag[A, JoulePerKiloGram](a)

  /**
    * Suppose we want a way to express mass using kilogram, because kg is the international standard of unit.
    * Normally we would pass in Double and call it a day, but we can’t distinguish that from other Double values.
    * Can we use case class for this?
    *
    * case class KiloGram(value: Double)
    *
    * Although it does adds type safety, it’s not fun to use because we have to call x.value every time we need to extract the value out of it.
    *
    * Tagged type to the rescue.
    **/

  sealed trait KiloGram

  sealed trait JoulePerKiloGram

  energyR(mass)

  // energyR(20.0)  Compile time error


  /**
    * So now that there are two equally valid ways for numbers (addition and multiplication) to be monoids,
    * which way do choose? Well, we don’t have to.
    * This is where Scalaz 7.1 uses tagged type.
    * The built-in tags are Tags.
    * There are 8 tags for Monoids and 1 named Zip for Applicative.
    * (Is this the Zip List I couldn’t find yesterday?)
    **/


}
