package router

import akka.http.scaladsl.model.{DateTime, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import devTests.{GetHealthRequest, HealthResponse, RequestHandler}
import model.EventCreator
import utils.{ActorInitializer, Authenticator, JsonMarshalling}

/**
  * Just an handler for health-checking the application
  */
object PingRouter extends JsonMarshalling {
  import ActorInitializer._
  val requestHandler = system.actorOf(RequestHandler.props(), "requestHandler")
  def apply () : Route = {
    path("ping") {
      get {
          onSuccess(requestHandler ? GetHealthRequest) {
            case response: HealthResponse =>
              complete(StatusCodes.OK, response.health)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
      }
    }
  }
}
