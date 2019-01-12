package com.navneetgupta.scala.recursionschemes

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
    case JsStr(v) => s"\"$v\""
    case JsNum(v) => v.toString
    case JsBool(v) => if(v) "true" else "false"
    case JsArr(v) => v.map(serialise).mkString("[",",", "]")
    case JsObj(v) => v.mapValues(serialise).map(f).mkString()
  }

  def fmtKv
}
