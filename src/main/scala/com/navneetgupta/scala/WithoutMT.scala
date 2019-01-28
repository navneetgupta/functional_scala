package com.navneetgupta.scala

import scala.concurrent.{ExecutionContext, Future}

object WithoutMT {
  case class User(name: String, email: String, id: Long, surName: Option[String] = None, canBeUpdated: Boolean = false)


  def checkUserExist(id: Long): Future[Option[User]] = ???
  def canBeUpdated(u: User) : Future[Boolean] = ???
  def updateUserOnMap(u: User): Future[Option[User]] = ???
  def updateUserOnMap2(u: User): Future[User] = ???

  implicit val ec= ExecutionContext.global

  def updateUserNaive(u : User): Future[Option[User]] =
    checkUserExist(u.id).flatMap{ maybeUser =>
      maybeUser match {
        case Some(user) => canBeUpdated(user).flatMap(canbeUpdated =>
          if(canbeUpdated) updateUserOnMap2(user).map(Some(_))
          else Future{None}
        )
        case None => Future{None}
      }}


  def updateUserOnMap3(u: User): Future[Either[MyError, User]] = ???

  case class MyError(msg: String)

  def updateUserNaive2(u : User): Future[Either[MyError, User]] =
    checkUserExist(u.id).flatMap{ maybeUser =>
      maybeUser match {
        case Some(user) => canBeUpdated(user).flatMap(canbeUpdated =>
          if(canbeUpdated) updateUserOnMap2(user).map(Right(_))
          else Future{Left(MyError("User Can't be Updated"))}
        )
        case None => Future{Left(MyError("User Doesn't Exist"))}
      }}
}
