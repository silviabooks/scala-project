package resources

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.DateTime

object EventHandler{
  def props(): Props = {
    Props(classOf[EventHandler])
  }
}

case class Event(
                name: String,
                category: String,
                date: DateTime,
                description: String
                )

case class CreateEvent(e: Event)

// temporary response (To be modified)
case class Response(temp: String)

class EventHandler extends Actor with ActorLogging{
  var events : Array[Event] = Array()
  var tempString: String = "Event created!"
  override def receive: Receive = {
    case req : CreateEvent =>
      // Create a new user (add it in the DB?)
      var event = req.e
      events :+= event
      Console.println(event)
      sender() ! event
  }
}