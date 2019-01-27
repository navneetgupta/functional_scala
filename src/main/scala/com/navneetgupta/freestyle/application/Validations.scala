package com.navneetgupta.freestyle.application

import cats.data.{NonEmptyList, OptionT, ValidatedNel}
import freestyle.free.FreeS
import freestyle.free._

object Validations {
  import Modules._
  import cats.implicits._

  def validateOrder[F[_]](order: Order, customer: Customer)(implicit app: App[F]): FreeS.Par[F, ValidatedNel[String, Unit]] = {
    app.readerM.reader { config =>
      val v = ().validNel[String]
      v.ensure(NonEmptyList.one("Number of carets order should be greater than 0"))(_ => order.crates > 0) |+|
      v.ensure(NonEmptyList.one("Item Variety should be the valid one"))(_ => config.varities.contains(order.variety.toLowerCase))
    }
  }

  def getCustomer[F[_]](id: CustomerID)(implicit app: App[F]) = {
    OptionT[FreeS[F, ?], Customer](app.cacheM.get(id).freeS).orElseF {
      for {
        customer <- app.persistence.customer.getCustomer(id).freeS
        _ <- customer.fold(().pure[FreeS[F, ?]])(cust => app.cacheM.put(id, cust))
      } yield customer
    }.value
  }
}
