package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._,Scalaz._, iteratee._, Iteratee._

object IterateeEx extends App {
  //Most programmers have come across the problem of treating an I/O data source (such as a file or a socket) as a data structure. This is a common thing to want to do. ... Instead of implementing an interface from which we request Strings by pulling, we’re going to give an implementation of an interface that can receive Strings by pushing. And indeed, this idea is nothing new. This is exactly what we do when we fold a list:

 def counter[E]: Iteratee[E, Int] = {
    def step(acc: Int)(s: Input[E]): Iteratee[E, Int] =
      s(el = e => cont(step(acc + 1)),
        empty = cont(step(acc)),
        eof = done(acc, eofInput[E])
      )
    cont(step(0))
  }

  println((counter[Int] &= enumerate(Stream(1, 2, 3))).run)

  println((length[Int, Id] &= enumerate(Stream(1, 2, 3))).run)
}

