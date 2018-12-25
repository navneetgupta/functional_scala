package com.navneetgupta.scalaz.zio

import scalaz.zio.{IO, Queue}

object QueueEx extends App {
  // Queue is a lightweight in-memory queue built on ZIO with composable and transparent back-pressure.
  // It is fully asynchronous (no locks or blocking), purely-functional and type-safe.

  // two basic operations: `offer`, which places an A in the Queue,
  // and `take` which removes and returns the oldest value in the Queue.
  val res: IO[Nothing, Int] = for {
    queue <- Queue.bounded[Int](100)
    _ <- queue.offer(1)
    v1 <- queue.take
  } yield v1

  // Bounded Queue Back Pressured Bound
  val queue: IO[Nothing, Queue[Int]] = Queue.bounded[Int](100)

  // Dropping Bounded Queue

  val dropingBounded: IO[Nothing, Queue[Int]] = Queue.dropping[Int](100)

  // Sliding Q
  val slidingQ: IO[Nothing, Queue[Int]] = Queue.sliding[Int](100)

  // UnBounded Q
  val unboundedQ : IO[Nothing, Queue[Int]] = Queue.unbounded[Int]

  val resp = for {
    queue <- Queue.bounded[Int](2)
    _ <- queue.offer(1)
    _ <- queue.offer(2)
    a <- queue.take
  } yield a

}
