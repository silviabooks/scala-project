package router

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import controllers._
import model.Ticket
import utils.{ActorInitializer, Authenticator, JsonMarshalling}

/**
  * Singleton object to provide routes for tickets resources
  */
object TicketsRouter extends JsonMarshalling {

  import ActorInitializer._
  val ticketHandler = system.actorOf(TicketHandler.props(), "ticketHandler")

  def apply () : Route = {
    pathPrefix("tickets") {
      authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user =>
        pathEnd {
          get {
            onSuccess(ticketHandler ? GetTickets(user)) {
              case r: Array[Ticket] =>
                complete(r)
              case r: StatusCode =>
                complete(r)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          } ~ post {
            entity(as[Ticket]) {
              ticket =>
                if (user.isAdmin | user._id.toString == ticket.boughtFrom.toString) {
                  onSuccess(ticketHandler ? CreateTicket(ticket)) {
                    case r: StatusCode =>
                      complete(r)
                  }
                } else complete(StatusCodes.Unauthorized)
            }
          }
        } ~ path(Segment) { ticketId =>
          get {
            onSuccess(ticketHandler ? GetTicket(ticketId, user)) {
              case r: Array[Ticket] =>
                complete(r)
              case r: StatusCode =>
                complete(r)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          } ~ delete {
            if (user.isAdmin) {
              onSuccess(ticketHandler ? DeleteTicket(ticketId)) {
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
