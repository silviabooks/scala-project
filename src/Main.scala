package org.unict.ing.advlanguages.boxoffice


import akka.actor.ActorSystem
import akka.http.scaladsl.model.{DateTime, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask
import resources._

import scala.concurrent.duration._
import scala.io.StdIn
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.collection.mutable.ArrayBuffer

trait JsonUnMarshall extends SprayJsonSupport with DefaultJsonProtocol {

  // see https://stackoverflow.com/questions/25178108/converting-datetime-to-a-json-string

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    override def write(obj: DateTime) = JsString(obj.toIsoDateString())

    override def read(json: JsValue) : DateTime = {
      val x = DateTime.fromIsoDateTimeString(json.convertTo[String])
      x match {
        case Some(d) => d
        case None    => throw new DeserializationException("Error on date deserialization")
      }
    }
  }

  implicit val eventFormat  = jsonFormat4(Event)
  implicit val userFormat   = jsonFormat4(User)
  implicit val healthFormat = jsonFormat2(Health)
}

object Main extends JsonUnMarshall {

  val host = "localhost"
  val port = 8080

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(20.seconds)

    val requestHandler = system.actorOf(RequestHandler.props(), "requestHandler")
    val eventHandler   = system.actorOf(EventHandler.props(), "eventHandler")
    val userHandler    = system.actorOf(UserHandler.props(), "userHandler")

    val route : Route = {

      path("hello") {
        get {
          onSuccess(requestHandler ? GetHealthRequest) {
            case response: HealthResponse =>
              complete(StatusCodes.OK, response.health)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~
          post {
            entity(as[Health]) {
              statusReport =>
                onSuccess(requestHandler ? SetStatusRequest(statusReport)) {
                  case response: HealthResponse =>
                    complete(StatusCodes.OK, response.health)
                  case _ =>
                    complete(StatusCodes.InternalServerError)
                }
            }
          }
      } ~
        path("events") {
          get {
            onSuccess(requestHandler ? GetHealthRequest) {
              case response: HealthResponse =>
                complete(StatusCodes.OK, response.health)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          } ~
            post {
              entity(as[Event]) {
                event =>
                  onSuccess(eventHandler ? CreateEvent(event)) {
                    case response: Event =>
                      complete(StatusCodes.OK, response)
                    case _ =>
                      complete(StatusCodes.InternalServerError)
                  }
              }
            }
        } ~
        path("users") {
          get {
            onSuccess(userHandler ? GetUserList) {
              case response: ArrayBuffer[_] =>
                complete(StatusCodes.OK)
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
            } ~
          path("users" / LongNumber){
            userId =>
              get{
                onSuccess(userHandler ? GetSingleUser(userId)) {
                  case response: SingleUserResponse =>
                    complete(StatusCodes.OK, response.user)
                  case _ =>
                    complete(StatusCodes.InternalServerError)
                }
              }
          }

        }
    }

    val bindingFuture = Http().bindAndHandle(route, host, port)
    println("API on...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
