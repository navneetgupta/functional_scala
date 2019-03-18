package com.navneetgupta.others.tagless

import cats.effect.IO

object ModulePattern {
  trait Console{
    def consoleService: Console.Service
  }

  object Console {
    trait Service {
      def putStrLn(msg: String) : IO[Unit]
      def readLn: IO[String]
    }
  }

}

object ModulePatternProgram extends App {
  import ModulePattern._

//  def program
}
