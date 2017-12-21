package resources

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.DateTime
import model.{Event, Events}
import org.mongodb.scala.{Completed, Observer}

object EventHandler{
  def props(): Props = {
    Props(classOf[EventHandler])
  }
}

case class CreateEvent(e: Event)
case class GetEvents()
case class GetEvent(id : Int)
case class PutEvent(id : Int, e: Event)
case class DeleteEvent(id : Int)

class EventHandler extends Actor with ActorLogging{
  var events : Array[Event] = Array()
  //var tempString: String = "Event created!"
  override def receive: Receive = {
    case req : CreateEvent =>
      // Create a new user (add it in the DB?)
      //events :+= req.e
      //sender() ! events.last

      // In order to have the sender in the async scope of the observer
      val requester = context.sender()
      Events().insertOne(req.e).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = requester ! req.e
        override def onError(throwable: Throwable) = null
        override def onNext(tResult: Completed) = null
      })

    case _ : GetEvents =>
      val requester = context.sender()
      Events().find().collect().subscribe((results: Seq[Event]) => {
        println(s"results retrieved ${results}")
        requester ! results.toArray
      })

    case e : GetEvent =>
      sender() ! events(e.id)
    case req : PutEvent =>
      events(req.id) = req.e
      sender() ! events(req.id)
    case e : DeleteEvent =>
      events(e.id) = null
  }

}