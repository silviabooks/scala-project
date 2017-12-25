package org.unict.ing.advlanguages.boxoffice

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import router._
import scala.io.StdIn
import utils.{ActorInitializer, JsonMarshalling}

object Main extends App {
  import ActorInitializer._

  val host = "localhost"
  val port = 8080

  val route : Route = {
    PingRouter()    ~
    EventsRouter()  ~
    UsersRouter()   ~
    TicketsRouter() ~
    WSRouter()
  }

  val bindingFuture = Http().bindAndHandle(route, host, port)

  println("API on...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
