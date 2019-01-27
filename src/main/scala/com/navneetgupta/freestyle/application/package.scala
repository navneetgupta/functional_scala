package com.navneetgupta.freestyle

import java.util.UUID

package object application {
  type CustomerID = UUID
  case class Customer(id: CustomerID, name: String)
  case class Order(crates: Int, variety: String, customerId: CustomerID)
  case class Config(varities: Set[String])
}
