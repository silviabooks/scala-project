package model

import java.util.Date

import akka.http.scaladsl.model.DateTime
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

case class Event(
  _id:         ObjectId,
  name:        String,
  date:        DateTime,
  category:    String,
  description: String,
  quantity:    Int,
  price:       Double // Double has a standard Codec for Mongo Scala Driver, but BigDecimal
)

object EventCreator {
  def apply(name: String, date: DateTime, category: String, description: String, quantity: Int, price: Double) : Event = {
    Event(new ObjectId(), name, date, category, description, quantity, price)
  }
}

// Events is a singleton that returns the collection in the MongoDB db
object Events {
  val collection: MongoCollection[Event] = BoxOffice().getCollection("events")
  def apply() : MongoCollection[Event] = collection
}
