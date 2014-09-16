package org.rwjson

import play.api.libs.json.{JsValue, Writes}


package object dropempty extends DropEmptyJson {

  case class JsProperty(key: String, value: Option[JsValue])

  trait DropEmptyWrites[A] extends Writes[A] with DropEmptyJson

}
