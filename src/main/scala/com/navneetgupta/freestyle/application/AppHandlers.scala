package com.navneetgupta.freestyle.application

import java.util.UUID

import cats.data.Kleisli
import cats.effect.IO
import cats.implicits._
import com.navneetgupta.freestyle.application.Algebra._
import freestyle.free.cache.KeyValueMap
import freestyle.free.cache.hashmap._

object AppHandlers {

  type Stack[A] = Kleisli[IO, Config, A]

  val customerId1 = UUID.fromString("00000000-0000-0000-0000-000000000000")

  implicit val customerPersistenceHandler = new CustomerPersistence.Handler[Stack] {

    val customerMap : Map[CustomerID, Customer] = Map(customerId1 -> Customer(customerId1, "Test Customer 1"))

    def getCustomer(id: CustomerID): Stack[Option[Customer]] = Kleisli(_ => IO.pure(customerMap.get(id)))
  }

  implicit val stockPersistenceHandler = new StockPersistence.Handler[Stack] {
    def checkQuantityAvailable(variety: String): Stack[Int] = Kleisli(_ =>
      IO.pure(variety.toLowerCase match {
        case "granny smith" => 500
        case "malike"       => 100
        case _              => 25
      })
    )

    override def registerOrder(order: Order): Stack[Unit] = Kleisli(_ => IO(println(s"$order Registered")))
  }

  implicit val freestyleHasherCustomerId: Hasher[CustomerID] =
    Hasher[CustomerID](_.hashCode)

  import Modules._
  import cats.{Id, ~>}

  implicit val cacheHandler: cacheP.CacheM.Handler[Stack] = {
    val rawMap : KeyValueMap[Id, CustomerID, Customer] = new ConcurrentHashMapWrapper[Id, CustomerID, Customer]

    val cacheIdToStack: (Id ~> Stack) = new (Id ~> Stack) {
      def apply[A](a: Id[A]): Stack[A] = a.pure[Stack]
    }

    cacheP.implicits.cacheHandler(rawMap, cacheIdToStack)
  }
}
