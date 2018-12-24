package com.navneetgupta.scalaz.free

import scalaz.Scalaz._
import scalaz.{Functor, _}


object FreeMonadEx2 extends App {

  //  Continued From FreeMonadEx
  //  our FixE already exists, too, and it’s called the Free monad:
  //  data Free f r = Free (f (Free f r)) | Pure r

  //  In Scalaz version, Free constructor is called Free.Suspend and Pure is called Free.Return.
  //  Let’s re-implement CharToy commands based on Free:

  sealed trait CharToy[+Next]
  object CharToy {
    case class CharOutput[Next](a: Char, next: Next) extends CharToy[Next]
    case class CharBell[Next](next: Next) extends CharToy[Next]
    case class CharDone() extends CharToy[Nothing]

    implicit val charToyFunctor: Functor[CharToy] = new Functor[CharToy] {
      def map[A, B](fa: CharToy[A])(f: A => B): CharToy[B] = fa match {
        case o: CharOutput[A] => CharOutput(o.a, f(o.next))
        case b: CharBell[A] => CharBell(f(b.next))
        case CharDone() => CharDone()
      }
    }

    def output(a: Char): Free[CharToy, Unit] = Free.liftF(CharOutput(a, Free.point[CharToy, Unit](())))
    def bell: Free[CharToy, Unit] = Free.liftF(CharBell(Free.point[CharToy, Unit](())))
    def done: Free[CharToy, Unit] = Free.liftF[CharToy, Unit](CharDone())
    def pointed[A](a: A) = Free.point[CharToy, A](a)
  }

  import CharToy._

  def showProgram[R: Show](p: Free[CharToy, R]): String =
    p.resume.fold({
      case CharOutput(a, next) =>
        "output " + Show[Char].shows(a) + "\n" + showProgram(next)
      case CharBell(next) =>
        "bell " + "\n" + showProgram(next) case CharDone() =>
        "done\n"
      },
    {
      r: R => "return " + Show[R].shows(r) + "\n"
    })

  val subroutine = output('A')

  val program = for {
    _ <- subroutine
    _ <- bell
    _ <- done
  } yield ()


  def pretty[R: Show](p: Free[CharToy, R]) = print(showProgram(p))

  pretty(program)
  pretty(output('A'))
  pretty(pointed('A') >>= output)
  pretty(output('A') >>= pointed)

//  pretty((output('A') >> done) >> output('C'))
//  pretty(output('A') >> (done >> output('C')))

  //  data Free f r = Free (f (Free f r)) | Pure r
  //  data List a = Cons (a (List a )) | Nil

  //  In other words, we can think of a free monad as just being a list of functors.
  //  The Free constructor behaves like a Cons, prepending a functor to the list,
  //  and the Pure constructor behaves like Nil, representing an empty list (i.e. no functors).

  // The free monad is the interpreter’s best friend.
  // Free monads “free the interpreter” as much as possible while still maintaining the bare minimum necessary to form a monad.
  // On the flip side, from the program writer’s point of view,
  // free monads do not give anything but being sequential.
  // The interpreter needs to provide some run function to make it useful.
  // The point, I think, is that given a data structure that satisfies Functor, Free provides minimal monads automatically.
  // Another way of looking at it is that Free monad provides a way of building a syntax tree given a container.

}

