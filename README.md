# RWJson for Play

A few bits to make working with JSON in Play a bit easier.

## Drop Empty JSON

Consider this common scenario:

```
case class Person(name: String, age: Option[Int])

implicit def PersonWrites = new Writes[Person] {
  def writes(person: Person) = Json.obj(
    "name" -> person.name,
    "age" -> person.age
  )
}
```

What happens if a person's age is set to `None`?  Out of the box, the JSON writers will require that a
`Writes[Option[A]]` will be implemented.  More than likely it will end up looking something like:

```
implicit def OptionWrites(implicit aWrites: Writes[A]) = new Writes[Option[A]] {
  def writes(opt: Option[A]) = opt match {
    case None => JsUndefined
    case Some(a) => aWrites.writes(a)
  }
}
```

This results in the JSON rendering out with `undefined` values:

```
// Person("Bob", None)
{
  "name": "Bob",
  "age": undefined
}
```

Often times it would be more preferable to just omit the object properties for which there aren't a value:

```
// Person("Bob", None)
{
  "name": "Bob"
}
```

This behavior is provided in `DropEmptyJson`.  The simpliest way to use it is to replace any instance of `Writes[A]` with
`DropEmptyWrites[A]`.

```
// BEFORE:
implicit def PersonWrites = new Writes[Person] {
  def writes(person: Person) = ...
}

// AFTER:
implicit def PersonWrites = new DropEmptyWrites[Person] {
  def writes(person: Person) = ...
}
```

### Default Behavior

The following are considered "empty" by default:

  * `None`
  * `Nil` (an empty `Seq`)
  * A `JsObject` with no properties

### Extending Empty

For any large project it will probably be necessary to extend the definition of what is considered empty.  This can be
accomplished by defining an implicit function with a type signature of `(String, A) => JsProperty` (where `A` is the type
for which empty will be defined).

##### Example

```
trait MultipleChoiceAnswer
case class Answer(value: Char) extends MultipleChoiceAnswer
case object NoAnswer extends MultipleChoiceAnswer
```

In this case it might be necessary to consider `NoAnswer` as empty.  Defining the following implicit function in scope will allow that behavior:

```
implicit def MultipleChoiceAnswerToJsProperty[A](kv: (String, MultipleChoiceAnswer))(implicit json: Writes[MultipleChoiceAnswer]) = kv match {
  case (key, NoAnswer)  => JsProperty(key, None)
  case (key, a: Answer) => JsProperty(key, json.writes(a))
}
```

As long as `(String, A)` can be implicitly converted to `JsProperty`, the behavior of dropping empty values will work.  Note that in the implementation
of `def MultipleChoiceAnswerToJsProperty`, the value does not define the `Writes[MultipleChoiceAnswer]`.  This allows a separation of defining what values
are "empty" and defining how "non-empty" values are written.