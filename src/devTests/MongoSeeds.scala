package devTests

import controllers._
import model._

import scala.collection.mutable.ListBuffer
import akka.http.scaladsl.model.DateTime
import akka.pattern.ask
import utils.ActorInitializer

object MongoSeed extends App with ActorInitializer {
  println("Dropping collections and seeding... ")

  // Dropping collections
  Tickets().drop()
  Events().drop()
  Users().drop()

  // Initialization of the actors
  val eventHandler   = system.actorOf(EventHandler.props(), "eventHandler")
  val userHandler    = system.actorOf(UserHandler.props(), "userHandler")
  val ticketHandler = system.actorOf(TicketHandler.props(), "ticketHandler")

  // Creating the objects to store into MongoDB
  val events : ListBuffer[Event] = new ListBuffer[Event]()
  val users : ListBuffer[User] = new ListBuffer[User]()

  events += EventCreator("Roger Waters", DateTime(2018, 7, 22, 22, 0), "Rock", "The wall", 200, 60.50)
  events += EventCreator("Ian Anderson", DateTime(2018, 4, 22, 20, 0), "Rock", "Locomotive Breath", 400, 60.50)
  events += EventCreator("Camel", DateTime(2018, 7, 12, 21, 0), "Rock", "Mirage", 250, 70.50)
  events += EventCreator("Hans Zimmer", DateTime(2018, 3, 10, 20, 0), "Classic", "Soundtracks", 200, 100)
  events += EventCreator("Alborosie", DateTime(2018, 7, 21, 22, 0), "Reggae", "Natural Mystic", 300, 60.50)
  events += EventCreator("Manuchao", DateTime(2018, 7, 19, 22, 0), "Reggae", "Clandestino", 200, 30.50)
  events += EventCreator("London Philharmonic Orchestra", DateTime(2018, 2, 20, 19, 0), "Classic", "Beethoven Syphony no. 7", 200, 20)
  events += EventCreator("Robert Plant", DateTime(2018, 7, 20, 22, 0), "Rock", "Kashmir", 200, 60.50)

  users += UserCreator("Alessandro", "123456", "aleskandro@scala")
  users += UserCreator("Silvia", "1235", "silvia@scala")

  // Pushing objects to database
  events.foreach((e : Event) => {
    eventHandler ? CreateEvent(e)
  })

  users.foreach((u : User) => {
    userHandler ? CreateUser(u)
  })

  // Creates a ticket for each user, event and push to database
  users.foreach((u : User) => {
    events.foreach((e : Event) => {
      ticketHandler ? CreateTicket(TicketCreator(u.name, u._id, e._id))
    })
  })
}
