package controllers

import java.util.regex.Pattern

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.StatusCodes
import com.mongodb.client.model.Filters
import model.{Event, Events}
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.{Completed, Observer}

import scala.util.Try

object EventHandler{
  def props(): Props = {
    Props(classOf[EventHandler])
  }
}

/**
  * Case class to match POST requests to /events and create new event
  * @param e
  */
case class CreateEvent(e: Event)

/**
  * Case class to match GET requests to /events
  *
  */
case class GetEvents()

/**
  * Case class to match GET requests to /events/[[id]]
  * @param id
  */
case class GetEvent(id : String)

/**
  * Case class to match PUT requests to /events/[[id]] and edit event details
  * @param id
  * @param e
  */
case class PutEvent(id : String, e: Event)

/**
  * Case class to match GET requests to /events/[[id]]
  * @param id
  */
case class DeleteEvent(id : String)

/**
  * Case class to match GET requests to /events/[[pattern]] and search for a matching event
  * @param pattern the pattern to match
  */
case class SearchEvent(pattern : String)

/**
  * Case class to match GET requests to /events/categories to return all the categories for the events stored
  */
case class GetCategories()

/**
  * The Controller Actor to handling /events requests
  */
class EventHandler extends Actor with ActorLogging {
  import utils.ActorInitializer._
  var events : Array[Event] = Array()
  //var tempString: String = "Event created!"
  override def receive: Receive = {
    case req : CreateEvent => // Success or Failure
      // In order to have the sender in the async scope of the observer
      val requester = context.sender()
      Events().insertOne(req.e).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = {
          system.eventStream.publish(req.e)
          requester ! StatusCodes.OK
        }
        override def onError(throwable: Throwable) = {
          log.error(throwable.getMessage())
          requester ! StatusCodes.BadRequest
        }
        override def onNext(tResult: Completed) = {}
      })
    case _ : GetEvents => // Array[Event]
      val requester = context.sender()
      Events().find().collect().subscribe((results: Seq[Event]) => {
        requester ! results.toArray
      })
    case e : GetEvent => // Array[Event]
      val requester = context.sender()
      val id : Try[BsonObjectId] = Try(BsonObjectId(e.id))
      id match {
        case scala.util.Success(id) =>
          Events().find(Filters.eq("_id", id)).collect().subscribe((results: Seq[Event]) => {
            if (results.isEmpty) {
              requester ! StatusCodes.NotFound
            } else {
              requester ! results.toArray
            }
          })
        case scala.util.Failure(_) => requester ! StatusCodes.BadRequest
      }
    case req : PutEvent => // Success or Failure
      val requester = context.sender()
      Events().replaceOne(Filters.eq("_id", BsonObjectId(req.id)), req.e).subscribe(new Observer[UpdateResult] {
        override def onError(e: Throwable): Unit = {
          log.error(e.getMessage())
          requester ! StatusCodes.BadRequest
        }
        override def onComplete(): Unit = requester ! StatusCodes.OK
        override def onNext(result: UpdateResult): Unit = {
        }
      })
    case e : DeleteEvent => // Success or Failure
      val requester = context.sender()
      Events().deleteOne(Filters.eq("_id", BsonObjectId(e.id))).subscribe(new Observer[DeleteResult] {
        override def onError(e: Throwable): Unit = {
          log.error(e.getMessage())
          requester ! StatusCodes.BadRequest
        }
        override def onComplete(): Unit = requester ! StatusCodes.OK
        override def onNext(result: DeleteResult): Unit = {}
      })
    case request : SearchEvent => // Array[Event]
      val requester = context.sender()
      val pattern   = Pattern.compile(s"(?i).*${request.pattern}.*")
      Events().find(Filters.or(
        Filters.regex("name", pattern),
        Filters.regex("description", pattern)
      )).collect().subscribe(new Observer[Seq[Event]] {
        override def onError(e: Throwable): Unit = {
          println(e.getMessage())
        }
        override def onComplete(): Unit = {}
        override def onNext(result: Seq[Event]): Unit = {
          requester ! result.toArray
        }
      })

    case GetCategories => // Array[String]
      val requester = context.sender()
      Events().find().collect().subscribe((results : Seq[Event]) => {
        var categories : Array[String] = Array()
        results.sortWith((e1 : Event, e2 : Event) => {
          e1.category <= e2.category
        }).foreach((e : Event) => {
            categories :+= e.category
        })
        requester ! categories.distinct
      })
    case _ =>
      sender() ! StatusCodes.InternalServerError
  }
}
