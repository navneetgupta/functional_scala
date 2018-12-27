package com.navneetgupta.scala.examples.Free

object FreeMonadApp extends App{

  //   minimal Set For Monad
  trait Monad[F[_]] {
    def pure[A](a: A): F[A]
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  }

  object Monad {
    def apply[F[_]](implicit M: Monad[F]) = M
  }

  // Laws
  // pure(a).flatMap(f) == f(a) -- Left Identity
  // fa.flatMap(pure) == fa     -- Right Identity
  // fa.flatMap(f).flatMap(g) == fa.flatMap(a => f(a).flatMap(g)) -- Associativity

  sealed abstract class Free[F[_], A]
  final case class Pure[F[_], A](a: A) extends Free[F, A]
  final case class FlatMap[F[_], A, B](
      fa: Free[F, A],
      f: A => Free[F, B]
  ) extends Free[F, B]
  final case class Inject[F[_], A](fa: F[A]) extends Free[F, A]

  //  implicit def freeMonad[F[_], A]: Monad[Free[F, ?]] =
  //    new Monad[Free[F, ?]] {
  //      def pure[A](x: A): Free[F, A] = Pure(x)
  //      def flatMap[A,B](fa: Free[F, A])(f: A => Free[F, B]): Free[F, B] = FlatMap(fa,f)
  //    }


    implicit def freeMonad2[F[_], A]: Monad[Free[F, ?]] =
      new Monad[Free[F, ?]] {
        def pure[A](x: A): Free[F, A] = Pure(x)
        def flatMap[A,B](fa: Free[F, A])(f: A => Free[F, B]): Free[F, B] = fa match {
          case Pure(x) => f(x)
          case Inject(ga) => FlatMap(Inject(ga), f)
          case FlatMap(ga, g) => FlatMap(ga, (a: Any) => FlatMap(g(a), f))
        }
      }

}

abstract class ~>[F[_], G[_]] {
  def apply[A](input: F[A]): G[A]
}

object LawsIdentificationApp extends App {
  import FreeMonadApp._

  type Id[X] = X

  def example(f: Int => Free[Id, Int], g: Int => Free[Id, Int], fa: Free[Id, Int]) = {
    val exp1 = FlatMap(FlatMap(fa, f), g)
    val exp2 = FlatMap(fa, (a: Int) => FlatMap(f(a), g))

    exp1 != exp2
  }
}

object FreeSugar {
  import FreeFunctorApp._
  import FreeMonadApp._

  implicit def freeMonadFunctor[F[_]]: Functor[Free[F, ?]] = new Functor[Free[F, ?]] {
    def map[A, B](fa: Free[F, A])(f: A => B): Free[F, B] = Monad[Free[F, ?]].flatMap(fa)(a => Pure(f(a)))
  }

  implicit def toFreeOps[F[_], A](fa: Free[F, A]): FreeOps[F,A] = new FreeOps(fa)

  final class FreeOps[F[_], A](self: Free[F, A]) {
    def flatMap[B](f: A => Free[F, B]) = Monad[Free[F, ?]].flatMap(self)(f)
    def map[B](f: A => B): Free[F, B] = Functor[Free[F, ?]].map(self)(f)
  }
}

object FreeMonadTestApp extends App {
  import FreeMonadApp._
  import FreeSugar._

  sealed trait DslOps[A]
  final case class PutStrLn(s: String) extends DslOps[Unit]
  final case object ReadLn extends DslOps[String]

  type Dsl[A] = Free[DslOps, A]

  def putStrLn(s: String): Dsl[Unit] = Inject(PutStrLn(s))

  def readLn(): Dsl[String] = Inject(ReadLn)

  def program = for {
    s <- readLn()
    r <- putStrLn(s)
  } yield r

  println(program)
}