package com.navneetgupta.scalaz.others

import scala.language.higherKinds
import scalaz._, Scalaz._, concurrent._

object ToolBox {
  val length: String => Int = _.length

  val plus: (Int, Int) => Int = _ + _

  def lengthOperation[F[_]: Functor](str: F[String]): F[Int] = str.map(length)

  /// Fucntor map takes only one param but here we have two
  def plusOperation[F[_]: Functor](f1: F[Int], f2: F[Int]): F[Int] = ??? // {
  // We can do it by making a one param function using curry

  //val temp: F[Int => Int] = f1.map(plus.curried)
  //f2.map(temp) //we need to use Apply type class whihc extends Functor so it have map method in addition to ap method with signature
  // def ap[A,B](fa: => F[A])(f: => F[A=>B]): F[B] Whihc is waht we need so we can rewrite plusOperation as below
  // }

  def plusOperation1[F[_]: Apply](f1: F[Int], f2: F[Int]): F[Int] = {
    //Apply[F].ap(f2)(temp)

    //    val temp: F[Int => Int] = f1.map(plus.curried)
    //    f2 <*> temp

    // above two statment can be combined into one as below

    // Apply[F].apply2(f1, f2)(plus)

    (f1 |@| f2)(_ + _) // |@| from applicative builder
  }

  def plusOperation2[F[_]: Applicative](f1: F[Int], f2: Int): F[Int] =
    //(f1 |@| Applicative[F].point(f2))(plus)
    (f1 |@| f2.point[F])(plus)

  def plusOperation3[F[_]: Applicative](seed: String, f1: F[Int], f2: String => F[Int]): F[Int] =
    //(f1 |@| Applicative[F].point(f2))(plus)
    (f1 |@| f2(seed))(plus)

  def plusOperation4[F[_]: Bind](seed: F[String], f1: F[Int], f2: String => F[Int]): F[Int] =
    //(f1 |@| Bind[F].bind(seed)(f2))(plus)
    //(f1 |@| seed.bind(f2))(plus)
    (f1 |@| (seed >>= f2))(plus)

  def plusOperation5[F[_]: Bind](seed: F[String], f1: F[Int], f2: String => F[Int]): F[Int] =
    seed >>= (s =>
      (f1 >>= (a =>
        f2(s).map(b =>
          plus(a, b)))))

  // bind Operator >>= in scalaz has alias of flatMap
  def plusOperation6[F[_]: Bind](seed: F[String], f1: F[Int], f2: String => F[Int]): F[Int] =
    for {
      s <- seed
      a <- f1
      b <- f2(s)
    } yield (plus(a, b))

  // But we know For comprehension works only wiht Monad and exactly Monad is a typeclass whihc extends Applicative and Bind

  def sum[A: Semigroup](a1: A, a2: A): A = a1 |+| a2

  def sum2(list: List[Int]): Int = list.fold(0)(_ + _)

  def sum3[A: Semigroup](list: List[Int]): Int = list.fold(0)(_ |+| _)

  def sum[A: Monoid](list: List[A]): A = list.fold(Monoid[A].zero)(_ |+| _)

  def sum[F[_]: Foldable, A: Monoid](f: F[A]): A = f.fold

  val prblm1: Task[Task[Int]] = Task.delay { Task.delay { 10 } }

  val solution1: Task[Int] = prblm1.join

  val prblm2: List[Task[Int]] = List(Task.now(10), Task.now(20))

  val solution2: Task[List[Int]] = prblm2.sequence

  val ids = List(1, 2, 3)

  class User(val id: Int, val login: String)

  val fetchUsers: Int => Task[User] = (id: Int) => Task.delay {
    new User(id, "login $id")
  }

  val usersList: Task[List[User]] = ids.traverse(fetchUsers)

  def main(args: Array[String]): Unit = {
    val str = "hello"
    val mayBestr: Option[String] = str.some
    val strErr: Throwable \/ String = str.right
    val fetchStr: Task[String] = Task.delay {
      "hello"
    }

    type ErrType[A] = Throwable \/ A
    lengthOperation[Id](str)
    lengthOperation[Option](mayBestr)
    lengthOperation[ErrType](strErr)
    lengthOperation[Task](fetchStr)

    val i1 = 10
    val i2 = 20
    println(plusOperation1(i1.some, 20.some))

    println(plusOperation2(i1.some, i2))
    println(plusOperation3("hello", i1.some, length >>> (_.some)))
    println(plusOperation4("hello".some, i1.some, length >>> (_.some)))

    println(sum[List, Int](List(1, 2, 3)))
    println(sum[List, String](List("a", "b", "c")))
  }
}
