package utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Singleton with implicit vals needed for actor's context
  */
object ActorInitializer {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(20.seconds)
}
