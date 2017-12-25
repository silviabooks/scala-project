package utils


import akka.actor.Props
import akka.stream.actor.ActorPublisher
import model.Event

object WSPublisher {
  def props(): Props =
    Props(new WSPublisher())
}

/**
  * This is an ActorPublisher to provide Publish-Subscribe for new Events. Its role is to fill a broadcast queue for the
  * consumers/subscribers (all the clients connected) when a new [[model.Event]] is created.
  */
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

