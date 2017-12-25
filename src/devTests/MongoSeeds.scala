package devTests

import controllers._
import model._

import scala.collection.mutable.ListBuffer
import akka.http.scaladsl.model.DateTime
import akka.pattern.ask
import org.mongodb.scala.{Completed, Observer}
import utils.ActorInitializer

import scala.concurrent.Future
import scala.io.StdIn

/**
  * This Object can be runned. It automatically populates the Database for development purposes and demo testing
  */
object MongoSeed extends App {
  import ActorInitializer._
  println("Dropping collections and seeding... ")
  def generateObserver(messagePrefix : String) = new Observer[Completed] {
    override def onError(e: Throwable): Unit = println(s"[${messagePrefix}] ${e.getMessage}")
    override def onComplete(): Unit = println(s"[${messagePrefix}] Completed")
    override def onNext(result: Completed): Unit = println(s"[${messagePrefix}] ${result.toString()}")
  }

  def generateOnSuccessFuture(messagePrefix: String, f : Future[Any]) : Future[Any] = {
    f onSuccess {
      case r : Any =>
        println(s"[${messagePrefix}] ${r.toString}")
    }
    return f
  }

  // Dropping collections
  Tickets().drop().subscribe(generateObserver("Dropping Tickets"))
  Events().drop().subscribe(generateObserver("Dropping Events"))
  Users().drop().subscribe(generateObserver("Dropping Users"))

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

  users += UserCreator("Alessandro", "123456", "aleskandro@scala", true)
  users += UserCreator("Silvia", "1235", "silvia@scala", true)
  users += UserCreator("Pippo", "1235", "pippo@scala", false)
  users += UserCreator("Pluto", "1235", "pluto@scala", false)

  var i = 0
  // Pushing objects to database
  events.foreach((e : Event) => {
    i += 1
    generateOnSuccessFuture(s"Event#${i}", eventHandler ? CreateEvent(e))
  })

  i = 0
  var lastUserFuture : Future[Any] = null

  users.foreach((u : User) => {
    i += 1
    lastUserFuture = generateOnSuccessFuture(s"User#${i}", userHandler ? CreateUser(u))
  })

  // Creates a ticket for each user, event and push to database
  lastUserFuture onSuccess {
    case _ =>
      i = 0
      users.foreach((u: User) => {
        events.foreach((e: Event) => {
          i += 1
          generateOnSuccessFuture(s"Ticket#${i}", ticketHandler ? CreateTicket(TicketCreator(u.name, u._id, e._id)))
        })
      })
  }
  StdIn.readLine()
  system.terminate()
}
