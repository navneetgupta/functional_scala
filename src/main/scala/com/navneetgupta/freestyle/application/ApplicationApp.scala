package com.navneetgupta.freestyle.application

import com.navneetgupta.freestyle.application.AppExceptions._
import freestyle.free._
import freestyle.free.implicits._
import cats.implicits._
import cats.syntax._
import cats.effect.IO


object ApplicationApp extends App {
  import Modules._
  import Validations._
  import rd.implicits._
  import cats.mtl.implicits._
  import AppHandlers._
  import freestyle.free.effects.error.implicits._


  def processOrder[F[_]](order: Order)(implicit app: App[F]): FreeS[F, String] = {
    import app.persistence._, app.errorM._
    for {
      customerOpt <- getCustomer[F](order.customerId)
      customer <- either[Customer](customerOpt.toRight(CustomerNotFound(order.customerId)))
      validation <- validateOrder[F](order, customer)
      _ <- either(validation.toEither.leftMap(ValidationError))
      nbAvailable <- stock.checkQuantityAvailable(order.variety)
      _ <- either(
        Either.cond(
          order.crates <= nbAvailable,
          (),
          QuantityNotAvailable(
            s"""There are insufficient crates of ${order.variety} apples in stock
               |(only $nbAvailable available, while ${order.crates} needed - $order).""".stripMargin)
        )
      )
      _ <- stock.registerOrder(order)
    } yield s"Order registered for customer ${order.customerId}"
  }


  val program = processOrder[App.Op](Order(100, "malike", customerId1))
  val res = program.interpret[Stack]

  val varieties = Set("granny smith", "malike", "boskoop")

  val config = Config(varieties)

  println(res.run(config).unsafeRunSync())
}
