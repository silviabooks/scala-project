package resources

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.DateTime
//DateTime doc: https://github.com/akka/akka-http/blob/master/akka-http-core/src/main/scala/akka/http/scaladsl/model/DateTime.scala

/*
* The user can:
* - search events
* - book/buy tickets
* - subscribe to one or more event categories to get notification on a new event creation
* */

object UserHandler{
  def props(): Props = {
    Props(classOf[UserHandler])
  }
}

// I found something that could be useful...
// https://alvinalexander.com/scala/how-to-start-akka-actors-in-scala

// TODO: refine the User class (add other properties...)
case class User(
                 id: Int,
                 name: String
               )

//case class Event(name: String, category: String, description: String)
//case class Ticket(id: Int, date: DateTime)
case class CreateUser(u: User)

// temporary response (To be modified)
case class Response(temp: String)


class UserHandler extends Actor with ActorLogging{

  var tempString: String = "User created!"

  override def receive: Receive = {
    case CreateUser =>
      // Create a new user (add it in the DB?)
      Console.println(tempString)
      sender() ! Response(tempString)
    // TODO: add other cases
  }
}
