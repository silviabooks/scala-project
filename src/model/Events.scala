package model

import akka.http.scaladsl.model.DateTime
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

/**
  *
  * @param _id the ObjectId
  * @param name The Name of the [[Event]]
  * @param date The DateTime for the [[Event]]
  * @param category A category for the [[Event]]
  * @param description A long description for the [[Event]]
  * @param quantity The quantity of available tickets for the [[Event]]
  * @param price The price for a [[Ticket]]
  */
case class Event(
  _id:         ObjectId,
  name:        String,
  date:        DateTime,
  category:    String,
  description: String,
  quantity:    Int,
  price:       Double // Double has a standard Codec for Mongo Scala Driver, but BigDecimal
)

/**
  * [[EventCreator]] is a Factory to create a new [[Event]]
  */
object EventCreator {
  def apply(name: String, date: DateTime, category: String, description: String, quantity: Int, price: Double) : Event = {
    Event(new ObjectId(), name, date, category, description, quantity, price)
  }
}

/**
  * Events is a singleton that returns the collection in the MongoDB db
  */
object Events {
  val collection: MongoCollection[Event] = BoxOffice().getCollection("events")
  def apply() : MongoCollection[Event] = collection
}
