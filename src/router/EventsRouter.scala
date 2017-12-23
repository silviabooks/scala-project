package router

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route}
import akka.pattern.ask
import model.Event
import controllers._
import utils.{ActorInitializer, JsonMarshalling}

object EventsRouter extends JsonMarshalling with ActorInitializer {
  val eventHandler = system.actorOf(EventHandler.props(), "eventHandler")

  def apply () : Route = {
    pathPrefix("events") {
      pathEnd {
        get {
          onSuccess(eventHandler ? GetEvents()) {
            case r: Array[Event] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
          entity(as[Event]) {
            event =>
              onSuccess(eventHandler ? CreateEvent(event)) {
                case r: StatusCode =>
                  complete(r)
              }
          }
        }
      } ~ path("search" / Segment) { pattern =>
        get {
          onSuccess(eventHandler ? SearchEvent(pattern)) {
            case r: Array[Event] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~ path("categories") {
        get {
          onSuccess(eventHandler ? GetCategories) {
            case r: Array[String] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      } ~ path(Segment) { eventId =>
        get {
          onSuccess(eventHandler ? GetEvent(eventId)) {
            case r: Array[Event] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ put {
          entity(as[Event]) { event =>
            onSuccess(eventHandler ? PutEvent(eventId, event)) {
              case r: StatusCode =>
                complete(r)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ delete {
          onSuccess(eventHandler ? DeleteEvent(eventId)) {
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }
}
