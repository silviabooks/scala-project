package devTests

import scala.concurrent.duration._
import controllers._
import model.{Event, EventCreator}
import scala.collection.mutable.ListBuffer
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{DateTime}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask
// Custom case classes codecs for mongo scala driver
// https://github.com/mongodb/mongo-scala-driver/blob/master/examples/src/test/scala/tour/QuickTourCaseClass.scala

object MongoSeed extends App {
  println("Starting seeding")
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(20.seconds)

  // Initialization of the actors
  val requestHandler = system.actorOf(RequestHandler.props(), "requestHandler")
  val eventHandler   = system.actorOf(EventHandler.props(), "eventHandler")
  val userHandler    = system.actorOf(UserHandler.props(), "userHandler")

  val Events : ListBuffer[Event] = new ListBuffer[Event]()

  Events += EventCreator("Roger Waters", DateTime(2018, 7, 22, 22, 0), "Rock", "The wall", 200, 60.50)
  Events += EventCreator("Ian Anderson", DateTime(2018, 4, 22, 20, 0), "Rock", "Locomotive Breath", 400, 60.50)
  Events += EventCreator("Camel", DateTime(2018, 7, 12, 21, 0), "Rock", "Mirage", 250, 70.50)
  Events += EventCreator("Hans Zimmer", DateTime(2018, 3, 10, 20, 0), "Classic", "Soundtracks", 200, 100)
  Events += EventCreator("Alborosie", DateTime(2018, 7, 21, 22, 0), "Reggae", "Natural Mystic", 300, 60.50)
  Events += EventCreator("Manuchao", DateTime(2018, 7, 19, 22, 0), "Reggae", "Clandestino", 200, 30.50)
  Events += EventCreator("London Philharmonic Orchestra", DateTime(2018, 2, 20, 19, 0), "Classic", "Beethoven Syphony no. 7", 200, 20)
  Events += EventCreator("Robert Plant", DateTime(2018, 7, 20, 22, 0), "Rock", "Kashmir", 200, 60.50)

  Events.foreach((e : Event) => {
    eventHandler ? CreateEvent(e)
  })
}
