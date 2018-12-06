package com.navneetgupta.learning.scalaz.monad.transformer

import scalaz._, Scalaz._


object ReaderTransformer extends App {
  def myName(step: String): Reader[String, String] = Reader {
    step + ", I'm " + _
  }

  def localExample: Reader[String, (String, String, String)] = for {
    name1 <- myName("First")
    name2 <- myName("Second") >=> Reader(_ + "dy")
    name3 <- myName("Third")
  } yield (name1, name2, name3)

  println(localExample("Fred"))


  type ReaderTOption[A, B] = ReaderT[Option, A, B]

  object ReaderTOption extends KleisliInstances {
    def apply[A, B](f: A => Option[B]): ReaderTOption[A, B] = Kleisli(f)
  }

  def configure(key: String) = ReaderTOption[Map[String, String], String] {
    _.get(key)
  }

  def setUpConnection = for {
    host <- configure("host")
    port <- configure("port")
    username <- configure("username")
  } yield (host, port, username)

  val goodConfig = Map("host" -> "eed3si9n.com",
    "username" -> "sa",
    "password" -> "****",
    "port" -> "8084"
  )

  println(setUpConnection(goodConfig))

  val badConfig = Map("" +
    "host" -> "example.com",
    "username" -> "sa"
  )

  println(setUpConnection(badConfig))

  /**
    * When we stack a monad transformer on a normal monad, the result is another monad. This suggests the possibility that
    * we can again stack a monad transformer on top of our combined monad, to give a new monad, and in fact this is a common thing to do.
    **/

  type StateTReaderTOption[C, S, A] = StateT[({type l[X] = ReaderTOption[C, X]})#l, S, A]

  //  object StateTReaderTOption extends StateTInstances with StateTFunctions {
  //
  //    def apply[C, S, A](f: S => (S, A)) = new StateT[({type l[X] = ReaderTOption[C, X]})#l, S, A]{
  //      def apply(s: S) = f(s).point[({type l[X] = ReaderTOption[C, X]})#l]
  //    }
  //    def get[C, S]: StateTReaderTOption[C, S, S] = StateTReaderTOption { s => (s, s) }
  //    def put[C, S](s: S): StateTReaderTOption[C, S, Unit] = StateTReaderTOption { _ => (s, ()) }
  //  }

}
