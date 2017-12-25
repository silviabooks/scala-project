package router

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import model.Event
import controllers._
import utils.{ActorInitializer, Authenticator, JsonMarshalling}

/**
  * Singleton to provide routes for events resource
  */
object EventsRouter extends JsonMarshalling {

  import ActorInitializer._
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
          authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user => // Only admin
            if (user.isAdmin) {
              entity(as[Event]) {
                event =>
                  onSuccess(eventHandler ? CreateEvent(event)) {
                    case r: StatusCode =>
                      complete(r)
                  }
              }
            } else complete(StatusCodes.Unauthorized)
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
          authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user => // Admin
            if (user.isAdmin) {
              entity(as[Event]) { event =>
                onSuccess(eventHandler ? PutEvent(eventId, event)) {
                  case r: StatusCode =>
                    complete(r)
                  case _ =>
                    complete(StatusCodes.InternalServerError)
                }
              }
            } else complete(StatusCodes.Unauthorized)
          }
        } ~ delete {
          authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user => // Admin
            if (user.isAdmin) {
              onSuccess(eventHandler ? DeleteEvent(eventId)) {
                case r: StatusCode =>
                  complete(r)
                case _ =>
                  complete(StatusCodes.InternalServerError)
              }
            } else complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
  }
}
