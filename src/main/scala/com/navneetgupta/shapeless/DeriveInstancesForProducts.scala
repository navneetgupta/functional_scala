package com.navneetgupta.shapeless

import com.navneetgupta.shapeless.AutoTypeClassInstancesEx.IceCream
import shapeless.{::, Generic, HList, HNil}

object DeriveInstancesForProducts  extends App {
  import CsvEncoderInstances._

  implicit val hnilEncoder: CsvEncoder[HNil] = CsvEncoder.createEncoder(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit
                                            hEncoder: CsvEncoder[H],
                                            tEncoder: CsvEncoder[T]
                                          ): CsvEncoder[H :: T] = CsvEncoder.createEncoder {
    case h :: t => hEncoder.encode(h) ++ tEncoder.encode(t) // :: Should be from hList not the list Cons operator.
  }

  val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] = implicitly

  println(reprEncoder.encode("abc" :: 123  :: false :: HNil))

//  implicit val iceCreamEncoder: CsvEncoder[IceCream] = {
//    val gen = Generic[IceCream]
//    val enc = CsvEncoder[gen.Repr]
//    CsvEncoder.createEncoder[IceCream](iceCream => CsvEncoder[gen.Repr])
//  }

//  implicit def genericEncoder[A](implicit
//                                gen : Generic[A],
//                                enc : CsvEncoder[???]
//                                ): CsvEncoder[A] = CsvEncoder.createEncoder(a => enc.encode(a))

//  implicit def genericEncoder[A](implicit
//                                 gen : Generic[A],
//                                 enc : CsvEncoder[gen.Repr]
//                                ): CsvEncoder[A] = CsvEncoder.createEncoder(a => enc.encode(a))

//  implicit def genericEncoder[A, R](implicit
//                                 gen : Generic[A]{ type Repr = R},
//                                 enc : CsvEncoder[R]
//                                ): CsvEncoder[A] = CsvEncoder.createEncoder(a => enc.encode(gen.to(a)))

  implicit def genericEncoder[A, R](implicit
                                    gen : Generic.Aux[A, R], // object Generic { type Aux[A, R] = Generic[A] { type Repr = R}}
                                    enc : CsvEncoder[R]
                                   ): CsvEncoder[A] = CsvEncoder.createEncoder(a => enc.encode(gen.to(a)))



  val iceCreams: List[IceCream] = List(
    IceCream("Sundae", 1, false),
    IceCream("Cornetto", 0, true),
    IceCream("Banana Split", 0, false)
  )
  println(writeCSV(iceCreams)(
    genericEncoder(
      Generic[IceCream],
      hlistEncoder(stringEncoder,
        hlistEncoder(intEncoder, hlistEncoder(booleanEncoder, hnilEncoder))))))



  println("TEsting")


}
