package com.navneetgupta.scala.recursionschemes

import cats.Functor

object RecursionSchemeNormalApp extends App {
  sealed trait Json
  case object JsNull extends Json
  case class JsStr(str: String) extends Json
  case class JsNum(num: Long) extends Json
  case class JsBool(bool: Boolean) extends Json
  case class JsArr(arr: List[Json]) extends Json
  case class JsObj(map: Map[String,Json]) extends Json


  def serialise(json: Json): String = json match {
    case JsNull => "null"
    case JsStr(v) => s""" "$v" """
    case JsNum(v) => v.toString
    case JsBool(v) => if(v) "true" else "false"
    case JsArr(v) => v.map(serialise).mkString("[",", ", "]")
    case JsObj(v) => v.mapValues(serialise).map(fmtKv).mkString("{", ", " , "}")
  }

  def fmtKv(kv : (String,String)): String = {
    val (key,value) = kv
    s"$key : $value"
  }


  val json: JsObj = JsObj(Map("Student" ->
                      JsObj(Map( "Name" -> JsStr("Navneet"),
                                 "Age" -> JsNum(39),
                                 "Manager" -> JsArr(List(JsStr("Navneet"),JsStr( "Gupta"), JsStr("Navneet Gupta")))
                      ))
              ))


  println(serialise(json))
}

object RecursionSchemeParametrized extends App {
  sealed trait Json[+A]
  case object JsNull extends Json[Nothing]
  case class JsStr(str: String) extends Json[Nothing]
  case class JsNum(num : Long) extends Json[Nothing]
  case class JsBool(bool : Boolean) extends Json[Nothing]

  case class JsArr[A](arr: List[A]) extends Json[A]
  case class JsObj[A](obj: Map[String, A]) extends Json[A]

  object Json {
    implicit val JsonFunctor = new Functor[Json] {
      def map[A, B](fa: Json[A])(f:  A => B): Json[B] = fa match {
        case JsNull => JsNull
        case JsStr(value) => JsStr(value)
        case JsNum(value) => JsNum(value)
        case JsBool(value) => JsBool(value)
        case JsArr(values) => JsArr(values.map(f))
        case JsObj(values) => JsObj(values.mapValues(f))
      }
    }
  }


}
