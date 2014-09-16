package org.rwjson.dropempty

import org.specs2.mutable.Specification
import play.api.libs.json.JsNumber


class DropEmptyJsonSpec extends Specification with DropEmptyJson {

  "DropEmptyJson" should {

    "drop None values as object properties" in {
      val noValue: Option[String] = None
      val obj = Json.obj(
        "empty" -> noValue,
        "number" -> 4
      )

      obj.fields must haveLength(1)
      obj.fields.head mustEqual ("number", JsNumber(4))
    }

    "drop empty lists as object properties" in {
      val obj = Json.obj(
        "empty" -> Seq[String](),
        "number" -> 4
      )

      obj.fields must haveLength(1)
      obj.fields.head mustEqual ("number", JsNumber(4))
    }

    "drop empty objects as object properites" in {
      val obj = Json.obj(
        "empty" -> Json.obj(),
        "number" -> 4
      )

      obj.fields must haveLength(1)
      obj.fields.head mustEqual ("number", JsNumber(4))
    }

    "drop nested empty objects as object properites" in {
      val noValue: Option[String] = None
      val obj = Json.obj(
        "empty" -> Json.obj(
          "more-empty" -> Json.obj(
            "even-more-empty" -> Json.obj(
              "none" -> noValue
            )
          )
        ),
        "number" -> 4
      )

      obj.fields must haveLength(1)
      obj.fields.head mustEqual ("number", JsNumber(4))
    }
  }

}