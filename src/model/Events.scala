package model

import akka.http.javadsl.model.DateTime

import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

case class Event(
  _id:         ObjectId,
  name:        String,
  date:        String, // TODO FIX for DateTime and JSON (un)marshalling
  category:    String,
  description: String
)

object EventCreator {
  def apply(name: String, date: String, category: String, description: String) : Event = {
    Event(new ObjectId(), name, date, category, description)
  }
}

// Events is a singleton that returns the collection in the MongoDB db
object Events {
  val collection: MongoCollection[Event] = BoxOffice().getCollection("events")
  def apply() : MongoCollection[Event] = collection
}
