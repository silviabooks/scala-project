package resources

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.DateTime

object EventHandler{
  def props(): Props = {
    Props(classOf[EventHandler])
  }
}


// Maybe this should be just a class in our "model"
case class Event(
                name: String,
                category: String,
                date: DateTime,
                description: String
                )


case class CreateEvent(e: Event)
case class GetEvents()
case class GetEvent(id : Int)
case class PutEvent(id : Int, e: Event)
case class DeleteEvent(id : Int)

class EventHandler extends Actor with ActorLogging{
  var events : Array[Event] = Array()
  var tempString: String = "Event created!"
  override def receive: Receive = {
    case req : CreateEvent =>
      // Create a new user (add it in the DB?)
      events :+= req.e
      sender() ! events.last
    case _ : GetEvents =>
      sender() ! events
    case e : GetEvent =>
      sender() ! events(e.id)
    case req : PutEvent =>
      events(req.id) = req.e
      sender() ! events(req.id)
    case e : DeleteEvent =>
      events(e.id) = null
  }

}