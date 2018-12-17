package com.navneetgupta.learning.scalaz.origamiprogramming

import java.util.Date

import scalaz.Memo

object MemoizedEx extends App {

    /**
      * Given you have some space in RAM, we could trade some of the expensive calculations for space by caching the result. This is called memoization.
      * */

  val slowFib: Long => Long = {
    case 0 => 0
    case 1 => 1
    case x1 => slowFib(x1-2) + slowFib(x1-1)
  }

  (0 to 50).foldLeft(new Date().getTime)((a,b) => {
    val t1 = new Date().getTime
    println(s"Fib for $b =>  ${slowFib(b)} , time taken is ${t1-a}")
    t1
  })

  val memoizedFib: Long => Long = Memo.immutableHashMapMemo { // don't define a def instead of val it will kill the Memo purpose.
    case 0 => 0
    case 1 => 1
    case x1 => memoizedFib(x1-2) + memoizedFib(x1-1)
  }

  (0 to 50).foldLeft(new Date().getTime)((a,b) => {
    val t1 = new Date().getTime
    println(s"Fib for $b =>  ${memoizedFib(b)} , time taken is ${t1-a}")
    t1
  })

}
