package org.unict.ing.advlanguages.boxoffice

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Route
import router._

import scala.io.StdIn
import utils.{ActorInitializer, JsonMarshalling}

/**
  * The main application to be ran
  */
object Main extends App {
  import ActorInitializer._

  val host = "0.0.0.0"
  val port = 8080

  val route : Route = {
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", "*"),
      RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"),
      RawHeader("Access-Control-Request-Headers", "Authorization")) {
      PingRouter() ~
        EventsRouter() ~
        UsersRouter() ~
        TicketsRouter() ~
        WSRouter()
    }
  }
  val bindingFuture = Http().bindAndHandle(route, host, port)
  println("BoxOffice service on...")

}
