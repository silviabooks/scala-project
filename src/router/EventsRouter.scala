package router

import akka.actor.Status.Success
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import model.Event
import controllers._
import utils.{ActorInitializer, JsonMarshalling}

object EventsRouter extends JsonMarshalling with ActorInitializer {
  val eventHandler = system.actorOf(EventHandler.props(), "eventHandler")
  def apply () : Route = {
    path("events") {
      get {
        onSuccess(eventHandler ? GetEvents()) {
          case response: Array[Event] =>
            complete(StatusCodes.OK, response)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      } ~
      post {
        entity(as[Event]) {
          event =>
            onSuccess(eventHandler ? CreateEvent(event)) {
              case Success =>
                complete(StatusCodes.OK)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
        }
      }
    } ~
    path("events" / "search" / Segment) { pattern =>
      get {
        onSuccess(eventHandler ? SearchEvent(pattern)) {
          case r: Array[Event] =>
            complete(StatusCodes.OK, r)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~
    path("events" / "categories") {
      get {
        onSuccess(eventHandler ? GetCategories) {
          case r: Array[String] =>
            complete(StatusCodes.OK, r)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    } ~
    path("events" / Segment) { eventId =>
      get {
        onSuccess(eventHandler ? GetEvent(eventId)) {
          case r: Array[Event] =>
            complete(StatusCodes.OK, r)
          case r: StatusCode =>
            complete(r)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      } ~
      put {
        entity(as[Event]) { event =>
          onSuccess(eventHandler ? PutEvent(eventId, event)) {
            case Success =>
              complete(StatusCodes.OK)
            case a: Any =>
              print(a)
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~
      delete {
        onSuccess(eventHandler ? DeleteEvent(eventId)) {
          case Success =>
            complete(StatusCodes.OK)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}
