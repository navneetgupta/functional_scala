package com.navneetgupta.freestyle.application

import freestyle.free._
import freestyle.free.effects.reader
import freestyle.free.cache.KeyValueProvider
import Algebra._
import freestyle.free.effects.error._

object Modules {
  val rd = reader[Config]
  val cacheP = new KeyValueProvider[CustomerID, Customer]

  @module trait Persistence {
    val customer: Algebra.CustomerPersistence
    val stock: Algebra.StockPersistence
  }

  @module trait App {
    val persistence: Persistence
    val errorM: ErrorM
    val cacheM: cacheP.CacheM
    val readerM: rd.ReaderM
  }
}


