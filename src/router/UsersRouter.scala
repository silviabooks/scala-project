package router

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import controllers._
import model.User
import utils.{ActorInitializer, Authenticator, JsonMarshalling}

/**
  * Singleton object for users resources
  */
object UsersRouter extends JsonMarshalling {

  import ActorInitializer._
  val userHandler = system.actorOf(UserHandler.props(), "userHandler")
  def apply () : Route = {
    path("users") {
      get {
        authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user => // Only admin
          if (user.isAdmin) {
            onSuccess(userHandler ? GetUsers()) {
              case response: Array[User] =>
                complete(StatusCodes.OK, response)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }
      } ~
      post {
        entity(as[User]) {
          user =>
            onSuccess(userHandler ? CreateUser(user)) {
              case rx: StatusCode => complete(rx)
              case _ => complete(StatusCodes.InternalServerError)
            }
        }
      }
    } ~
    path("users" / Segment) { userId =>
      authenticateBasicAsync(realm = "Admin", Authenticator.adminPassAuthenticator) { user => // Only admin
        get {
          if (user._id.toString == userId | user.isAdmin) {
            onSuccess(userHandler ? GetUser(userId)) {
              case response: User =>
                complete(StatusCodes.OK, response)
              case null =>
                complete(StatusCodes.NotFound)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        } ~
          delete {
            onSuccess(userHandler ? DeleteUser(userId)) {
              case response: StatusCode => complete(response)
              case _ => complete(StatusCodes.InternalServerError)
            }
          } ~
          put {
            entity(as[User]) {
              user =>
                onSuccess(userHandler ? UpdateUser(user, userId)) {
                  case response: StatusCode => complete(response)
                  case _ => complete(StatusCodes.InternalServerError)
                }
            }
          }
      }
    }
  }
}
