package com.navneetgupta.scalaz.zio

import scalaz.zio.Schedule
import scalaz.zio.duration._

object ScheduleEx extends App {
  /**
    * Schedules allow you to define and compose flexible recurrence schedules, which can be used to
    * repeat actions, or retry actions in the event of errors.
    *
    * Repeat: IO#repeat, IO#repeatOrElse, IO#repeatOrElse0
    *
    * Retries: IO#retry, IO#retryOrElse, IO#retryOrElse0
    *
    * A Schedule[A, B] consumes input values of type A (errors in the case of retry, or values in the case of repeat),
    * and based on these values and internal state, decides whether to recur or conclude. Every decision is
    * accompanied by a (possibly zero) delay, indicating how much time before the next recurrence,
    * and an output value of type B.
    **/

  val forever = Schedule.forever
  val never = Schedule.never
  val upTo10 = Schedule.recurs(10) // recurs 10 times
  val spaced = Schedule.spaced(10.milliseconds) //recurs every 10 sec
  val exponential = Schedule.exponential(10.milliseconds) // recurs using exponential backoff:
  val fibonacci = Schedule.fibonacci(10.milliseconds) //// recurs using fibonacci backoff:

  // Modifies delay of a schedule
  val boosted = Schedule.spaced(1.second).delayed(_ + 100.milliseconds)


  //  Combines two schedules through intersection, by recurring only if
  //  both schedules want to recur, using the maximum of the two delays between recurrences:

  val expUpTo10 = Schedule.exponential(1.second) && Schedule.recurs(10)

  //  Combines two schedules through union, by recurring if either schedule wants to recur,
  //  using the minimum of the two delays between recurrences:

  val expCapped = Schedule.exponential(100.milliseconds) || Schedule.spaced(1.second)

  //  Combines two schedules sequentially, by following the first policy until it ends,
  //  and then following the second policy:

  val sequential = Schedule.recurs(10) <||> Schedule.spaced(1.second)

}
