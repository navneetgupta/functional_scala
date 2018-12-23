package com.navneetgupta.scalaz.free

import scalaz.Functor

object FreeMonadEx extends App {
  // http://www.haskellforall.com/2012/06/you-could-have-invented-free-monads.html

  sealed trait Toy[+A, +Next]
  case class Output[A, Next](a: A, next:Next) extends Toy[A, Next]
  case class Bell[Next](next: Next) extends Toy[Nothing, Next]
  case object Done extends Toy[Nothing, Nothing]


  println(Output('A', Done))

  println(Bell(Done))



  sealed trait CharToy[+Next]
  object CharToy {
    case class CharOutput[Next](a: Char, next: Next) extends CharToy[Next]
    case class CharBell[Next](next: Next) extends CharToy[Next]
    case object CharDone extends CharToy[Nothing]


    def output[Next](a: Char, next: Next): CharToy[Next] = CharOutput(a, next)
    def bell[Next](next: Next): CharToy[Next] = CharBell(next)
    def done[Next] : CharToy[Next] = CharDone
  }

  import CharToy._

  println(output('A', done))
  println(bell(done))
  println(done)

  case class Fix[F[_]](f: F[Fix[F]])
  object Fix {
    def fix(toy: CharToy[Fix[CharToy]]) = Fix[CharToy](toy)
  }

  import Fix._

  println(fix(output('A', fix(done))))
  println(fix(bell(fix(done))))
  println(fix(done))


  sealed trait FixE[F[_], E]
  object FixE {

    case class Fix[F[_], E](f: F[FixE[F, E]]) extends FixE[F, E]
    case class Throwy[F[_], E](e: E) extends FixE[F, E]

    def fix[E](toy: CharToy[FixE[CharToy, E]]): FixE[CharToy, E] = Fix[CharToy, E](toy)
    def throwy[F[_], E](e: E) : FixE[F, E] = Throwy(e)

    def catchy[F[_]: Functor, E1, E2](ex: => FixE[F, E1])
                                     (f: E1 => FixE[F, E2]): FixE[F, E2] = ex match {
      case Fix(x) => Fix[F, E2](Functor[F].map(x) {catchy(_)(f)}) case Throwy(e) => f(e)
    }
  }


  implicit val CharToyFunctor = new Functor[CharToy] {
    def map[A, B](fa: CharToy[A])(f: A => B) : CharToy[B] = fa match {
      case CharOutput(a,next) => CharOutput(a, f(next))
      case CharBell(next) => CharBell(f(next))
      case CharDone => CharDone
    }
  }
}
