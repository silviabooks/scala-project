package router

import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import model.Event
import utils.{JsonMarshalling, WSPublisher}

/**
  * Singleton object to enable WebSocket and Publish-Subscribe
  */
object WSRouter extends JsonMarshalling {
  val dataSource = Source.actorPublisher[Event](WSPublisher.props())
  val myFlow = Flow.fromSinkAndSource(Sink.ignore, dataSource map {d => TextMessage.Strict(eventFormat.write(d).toString())})
  def apply () : Route = {
    path("ws") {
      handleWebSocketMessages(myFlow)
    }
  }
}
