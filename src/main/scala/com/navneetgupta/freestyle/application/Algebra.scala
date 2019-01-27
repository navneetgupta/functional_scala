package com.navneetgupta.freestyle.application

import freestyle.free._

object Algebra {
  @free trait CustomerPersistence {
    def getCustomer(id: CustomerID): FS[Option[Customer]]
  }

  @free trait StockPersistence {
    def checkQuantityAvailable(variety: String): FS[Int]
    def registerOrder(order: Order): FS[Unit]
  }
}
