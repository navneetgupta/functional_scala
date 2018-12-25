package com.navneetgupta.scalaz.zio

import scalaz.zio.Queue
import scalaz.zio.console._

object UnboundedQEx extends App {
  for {
    queue <- Queue.unbounded[String]
    _ <- queue.offer("Please Accept it").forever.fork
    _ <- queue.take.flatMap(putStrLn).forever
  } yield ()
}
