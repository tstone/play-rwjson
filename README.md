# RWJson for Play

A few bits to make working with JSON in Play a bit easier.

## Main Features

### Drop Empty JSON

Consider this common problem scenario:

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