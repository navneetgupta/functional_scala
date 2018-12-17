package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._,Scalaz._, effect._, IO._

object IoMonad extends App {

  val action1 = for {
    _ <- putStrLn("Hello World!!!!!")
  } yield ()

  action1.unsafePerformIO()

  val action2 = IO {
    val source = scala.io.Source.fromFile("./build.sbt")
    source.getLines().toStream
  }

  println(action2.unsafePerformIO().toList.foreach(println(_)))

  // Composing these into programs is done monadically

  def program : IO[Unit] = for {
    line <- readLn
    _ <- putStrLn(line)
  } yield ()


  (program |+| program).unsafePerformIO()


  //  Enumeration-Based I/O with Iteratees

  // Most programmers have come across the problem of treating an I/O data source (such as a file or a socket) as a data structure. This is a common thing to want to do. ... Instead of implementing an interface from which we request Strings by pulling, we’re going to give an implementation of an interface that can receive Strings by pushing. And indeed, this idea is nothing new. This is exactly what we do when we fold a list:




}
