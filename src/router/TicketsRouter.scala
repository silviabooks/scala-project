package router

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import controllers._
import model.Ticket
import utils.{ActorInitializer, JsonMarshalling}

object TicketsRouter extends JsonMarshalling with ActorInitializer {
  val ticketHandler = system.actorOf(TicketHandler.props(), "ticketHandler")

  def apply () : Route = {
    pathPrefix("tickets") {
      pathEnd {
        get {
          onSuccess(ticketHandler ? GetTickets()) {
            case r: Array[Ticket] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ post {
          entity(as[Ticket]) {
            event =>
              onSuccess(ticketHandler ? CreateTicket(event)) {
                case r: StatusCode =>
                  complete(r)
              }
          }
        }
      } ~ path(Segment) { ticketId =>
        get {
          onSuccess(ticketHandler ? GetTicket(ticketId)) {
            case r: Array[Ticket] =>
              complete(r)
            case r: StatusCode =>
              complete(r)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ delete {
          onSuccess(ticketHandler ? DeleteTicket(ticketId)) {
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
