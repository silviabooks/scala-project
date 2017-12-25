package utils


import akka.actor.Props
import akka.stream.actor.ActorPublisher
import model.Event

object WSPublisher {
  def props(): Props =
    Props(new WSPublisher())
}

class WSPublisher extends ActorPublisher[Event] {
  import ActorInitializer._
 // https://stackoverflow.com/questions/35276254/akka-http-websockets-how-to-send-consumers-the-same-stream-of-data
  override def preStart = {
    system.eventStream.subscribe(self, classOf[Event])
  }

  override def receive: Receive = {
    case msg: Event =>
      if (isActive && totalDemand > 0) {
        // Pushes the message onto the stream
        onNext(msg)
      }
  }
}

