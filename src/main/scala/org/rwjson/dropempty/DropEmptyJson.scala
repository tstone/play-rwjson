package org.rwjson.dropempty

import play.api.libs.json.{JsObject, Writes}
import play.api.libs.{json => play}


trait DropEmptyJson {
  
  implicit def OptionToJsProperty[A](kv: (String, Option[A]))(implicit json: Writes[A]) = kv match {
    case (key, None)        => JsProperty(key, None)
    case (key, Some(value)) => JsProperty(key, Some(json.writes(value)))
  }

  implicit def JsObjectToJsProperty[A](kv: (String, JsObject)): JsProperty = {
    val (key, jsObj) = kv
    if (jsObj.fields.size > 0) JsProperty(key, Some(jsObj))
    else JsProperty(key, None)
  }
  
  implicit def SeqToJsProperty[A](kv: (String, Seq[A]))(implicit json: Writes[Seq[A]]) = kv match {
    case (key, Nil) => JsProperty(key, None)
    case (key, xs)  => JsProperty(key, Some(json.writes(xs)))
  }

  implicit def AnyToJsProperty[A](kv: (String, A))(implicit json: Writes[A]) = {
    JsProperty(kv._1, Some(json.writes(kv._2)))
  }


  object Json {
    def obj(properties: JsProperty*): JsObject = play.Json.obj(
      properties.map { case JsProperty(key, optValue) =>
        optValue.map { value =>
          key -> play.Json.toJsFieldJsValueWrapper(value)
        }
      }.flatten: _*
    )
  }

}