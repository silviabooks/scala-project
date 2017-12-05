package org.unict.ing.advlanguages.boxoffice

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.duration._
import scala.io.StdIn
import spray.json.DefaultJsonProtocol

trait HealthJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val healthFormat = jsonFormat2(Health)
}


object HelloWorld extends HealthJsonSupport {

  val host = "localhost"
  val port = 8080

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(20.seconds)

    // This is to get (un)marshalling in POST requests, see: https://doc.akka.io/docs/akka-http/current/scala/http/introduction.html#routing-dsl-for-http-servers
    implicit val healthFormat = jsonFormat2(Health)

    val requestHandler = system.actorOf(RequestHandler.props(), "requestHandler")
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
