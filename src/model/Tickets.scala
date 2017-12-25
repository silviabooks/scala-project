package model

import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

/**
  * The class that represents a Ticket.
  * This is also passed to the JsonFormat to provide (Un)Marshalling
  * @param _id
  * @param ticketHolder
  * @param boughtFrom
  * @param event
  */
case class Ticket(
  _id:         ObjectId,
  ticketHolder:String,
  boughtFrom:  ObjectId,
  event:       ObjectId
)

/**
  * Factory to Create a new Ticket
  */
object TicketCreator {
  def apply(ticketHolder: String, boughtFrom: ObjectId, event: ObjectId) : Ticket = {
    Ticket(new ObjectId(), ticketHolder, boughtFrom, event)
  }
}

/**
  * Ticket is a singleton that returns the collection in the MongoDB db
  */
object Tickets {
  val collection: MongoCollection[Ticket] = BoxOffice().getCollection("tickets")
  def apply() : MongoCollection[Ticket] = collection
}
