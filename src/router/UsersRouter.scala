package router

import akka.actor.Status.Success
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import controllers._
import model.{User}
import utils.{ActorInitializer, JsonMarshalling}

object UsersRouter extends JsonMarshalling with ActorInitializer {
  val userHandler = system.actorOf(UserHandler.props(), "userHandler")
  def apply () : Route = {
    path("users") {
      get {
        onSuccess(userHandler ? GetUsers()) {
          case response: Array[User] =>
            complete(StatusCodes.OK, response)
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      } ~
      post {
        entity(as[User]) {
          user =>
            onSuccess(userHandler ? CreateUser(user)) {
              case response: User =>
                complete(StatusCodes.OK, response)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
        }
      }
    } ~
    path("users" / Segment) { userId =>
      get {
        onSuccess(userHandler ? GetUser(userId)) {
          case response: User =>
            Console.println(s"Getting user with id #$userId...")
            if(response.equals(null)) {
              Console.println("User ID not found.")
              complete(StatusCodes.NotFound)
            } else {
              complete(StatusCodes.OK, response)
            }
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      } ~
      delete {
        onSuccess(userHandler ? DeleteUser(userId)) {
          case response: DeleteUserResponse =>
            Console.println(s"Deleting user with id #$userId...")
            if(response == null) {
              complete(StatusCodes.NotFound)
            } else {
              complete(StatusCodes.OK, "User deleted!")
            }
          case _ =>
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }
}
