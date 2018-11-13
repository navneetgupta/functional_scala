package com.navneetgupta.learning.scalaz.monad

import scalaz._, Scalaz._

object ValidationEx extends App {

  println("event 1 Ok".success[String])

  println("Event 1 Failed".failure[String])

  assert(
    ("event 1 ok".success[String] |@| "event 2 failed!".failure[String] |@| "event 3 failed!".failure[String]) {_ + _ + _}
      === Failure("event 2 failed!event 3 failed!"))

  // Unlike \/ monad which cut the calculation short, Validation keeps going and reports back all failures.
  // This probably would be useful for validating user’s input on an online bacon shop.

  //The problem, however, is that the error messages are mushed together into one string. Shouldn’t it be something like a list?

  // This is where NonEmptyList (or Nel for short) comes in:

  println("event 1 Ok".successNel[String])

  println("Event 1 Failed".failureNel[String])

  println(("event 1 ok".successNel[String] |@| "event 2 failed!".failureNel[String] |@| "event 3 failed!".failureNel[String]) {_ + _ + _})

  println(1.wrapNel)
  

}
