package controllers

import akka.actor.Status.{Failure, Success}
import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.DateTime
import com.mongodb.client.model.Filters
import model.{Event, Events}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.{Completed, Observer}

import scala.util.Try

object EventHandler{
  def props(): Props = {
    Props(classOf[EventHandler])
  }
}

case class CreateEvent(e: Event)
case class GetEvents()
case class GetEvent(id : String)
case class PutEvent(id : String, e: Event)
case class DeleteEvent(id : String)

class EventHandler extends Actor with ActorLogging{
  var events : Array[Event] = Array()
  //var tempString: String = "Event created!"
  override def receive: Receive = {
    case req : CreateEvent => // Success or Failure
      // In order to have the sender in the async scope of the observer
      val requester = context.sender()
      Events().insertOne(req.e).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = requester ! Success
        override def onError(throwable: Throwable) = requester ! Failure
        override def onNext(tResult: Completed) = null
      })

    case _ : GetEvents => // Array[Event]
      val requester = context.sender()
      Events().find().collect().subscribe((results: Seq[Event]) => {
        requester ! results.toArray
      })

    case e : GetEvent => // Array[Event]
      val requester = context.sender()
      Events().find(Filters.eq("_id", BsonObjectId(e.id))).collect().subscribe((results: Seq[Event]) => {
        requester ! results.toArray
      })
    case req : PutEvent => // Success or Failure
      print("I'mHere)")
      val requester = context.sender()
      Events().replaceOne(Filters.eq("_id", BsonObjectId(req.id)), req.e).subscribe(new Observer[UpdateResult] {
        override def onError(e: Throwable): Unit = {
          println(e.getMessage()) // TODO Log
          requester ! Failure
        }
        override def onComplete(): Unit = requester ! Success
        override def onNext(result: UpdateResult): Unit = {
        }
      })
    case e : DeleteEvent => // Bool
      val requester = context.sender()
      Events().deleteOne(Filters.eq("_id", BsonObjectId(e.id))).subscribe(new Observer[DeleteResult] {
        override def onError(e: Throwable): Unit = {
          println(e.getMessage()) //TODO Log
          requester ! Failure
        }
        override def onComplete(): Unit = requester ! Success
        override def onNext(result: DeleteResult): Unit = {}
      })
    case _ =>
      sender() ! Failure
  }

}