package com.navneetgupta.scala

import cats.data.{EitherT, OptionT}
import com.navneetgupta.scala.WithoutMT.MyError

import scala.concurrent.{ExecutionContext, Future}

object MonadTrasformer extends App {

  case class User(name: String, email: String, id: Long, surName: Option[String] = None, canBeUpdated: Boolean = false)
  case class Address(line1: String, line2: String, state: String, pincode: Int, user: User)

  val userMap: Map[Long, User] = Map(
    1L -> User("Navneet Kumar", "nkumar@nkumar.com", 1),
    2L -> User("Navneet Gupta", "ngupta@ngupta.com" ,2),
    3L -> User("Gupta Navneet", "gNavneet@gNavneet.com" ,3, Some("Gupta"), true))
  val addressMap: Map[User, Address] = Map(
    userMap.get(1).get -> Address("Line 1", "Line 2", "State 1", 123456, userMap.get(1).get),
    userMap.get(2).get -> Address("Another Line 1", "Another Line 2", "State 2", 123456, userMap.get(2).get))

  implicit val ec: ExecutionContext = ExecutionContext.global

  def findUserById(id: Long): Future[User] = Future {userMap.get(id).get}
  def findAddressByUser(user: User): Future[Address] = Future{addressMap.get(user).get}

  def findAddressByUserId(id: Long): Future[Address] =
    for {
      user    <- findUserById(id)
      address <- findAddressByUser(user)
    } yield address


  println(findAddressByUserId(1).onComplete(println(_)))

  println(findAddressByUserId(3).onComplete(println(_))) // Exception

  // We are forcing above findUserById/findAddressByUser to have the value using .get on option  userMap.get(id).get/ addressMap.get(user).get
  // Whihc will throw exception if incorrect ID not found.


  // Now lets make it optional

  def findUserByIdOption(id: Long): Future[Option[User]] = Future {userMap.get(id)}
  def findAddressByUserOption(user: User): Future[Option[Address]] = Future{addressMap.get(user)}


//  def findAddressByUserIdOption(id: Long): Future[Option[Address]] =
//    for {
//      user    <- findUserByIdOption(id)
//      address <- findAddressByUserOption(user)
//    } yield address

  // The above deifination will not work since  <- is just a fancy way of writing flatMap and that if you flatMap
  // over a Future[Option[User]] you get to work with a Option[User] .

  // but findAddressByUserOption takes a User


  // Inelligant Solution:

  def findAddressByUserIdOption2(id: Long): Future[Option[Address]] =
    findUserByIdOption(id).flatMap {
      case Some(user) => findAddressByUserOption(user)
      case None       => Future.successful(None)
    }

  // If you have two Functors for A and B (i.e. you know how to map over A and over B), you can compose them together,
  // without knowing anything else. This means that you can take A[B[X]] and derive a Functor[A[B[X]] by composing
  // Functor[B] and Functor[A]


  // Same thing is not correct for Monad.
  // Knowing how to flatMap over A[X] and B[X] doesn’t grant you the power of magically deriving a flatMap for A[B[X]]
  //
  //  It turns out this is a well known fact: Monads do not compose, at least not generically.


//  let’s write a wrapper for Future[Option[A]] that provides its own map and flatMap.


  case class FutOpt[A](value: Future[Option[A]]) {

    def map[B](f: A => B): FutOpt[B] =
      FutOpt(value.map(optA => optA.map(f)))
    def flatMap[B](f: A => FutOpt[B]): FutOpt[B] =
      FutOpt(value.flatMap(opt => opt match {
        case Some(a) => f(a).value
        case None => Future.successful(None)
      }))
  }

  def findAddressByUserIdOption3(id: Long): Future[Option[Address]] =
    (for {
      user    <- FutOpt(findUserByIdOption(id))
      address <- FutOpt(findAddressByUserOption(user))
    } yield address).value


  //If we look closely, we’ll realize we don’t need to know anything specific about the “outer” Monad
  // (Future and List from the previous examples). As long as we can map and flatMap over it, we’re fine.
  // On the other hand, see how we destructured the Option? That’s some specific knowledge about the “inner” Monad
  // (Option in this case) that we need to have.

  // The Above FutOpt is magically called OptionT or Option Monad Transformer

//  OptionT has two type parameters F and A, where F is the wrapping Monad and A is type inside Option:
  //  in other words, OptionT[F, A] is a flat version of F[Option[A]] that has its own map and flatMap.

  //Notice that OptionT is also a monad, so we can use it in a for-comprehension



  //

  println(findAddressByUserIdOption2(1).onComplete(println(_)))

  println(findAddressByUserIdOption2(3).onComplete(println(_)))

  println(findAddressByUserIdOption3(1).onComplete(println(_)))

  println(findAddressByUserIdOption3(3).onComplete(println(_)))



  def getUserById(id: Long): Future[Option[User]] = Future {userMap.get(id)}
  def getEmail(user: User): Future[String] = Future{user.email}
  def getSurName(user: User): Option[String] = user.surName

//  def getSurNameAndEmail(id: Long): Future[(String,String)] =
//    for {
//      user <- FutOpt(getUserById(id))
//      email <- FutOpt(getEmail(user))
//      surName <- FutOpt(getSurName(user))
//    } yield (email,surName)

  //Basically FutOpt is OptionT in functional Library

  import cats.implicits._

  def getSurNameAndEmail(id: Long): OptionT[Future,(String,String)] =
    for {
      user <- OptionT(getUserById(id))
      email <- OptionT.liftF(getEmail(user))
      surName <- OptionT.fromOption(getSurName(user))
    } yield (email,surName)





  // Cehck the naive verison in WithoutMT.scala file


  // Improvement Option[User] => Either[MyError, User]

  type ResultT[F[_], A] = EitherT[F, MyError, A]
  type FutureResult[A] = ResultT[Future, A]

  def checkUserExist(id: Long): FutureResult[User] = ???
  def canBeUpdated(u: User) : FutureResult[User] = ???
  def updateUserOnMap(u: User): FutureResult[User] = ???


  def updateUserNaive(u : User): FutureResult[User] =
    for {
      user          <-  checkUserExist(u.id)
      _             <-  canBeUpdated(user)
      updatedUser   <-  updateUserOnMap(user)
    } yield updatedUser

}



