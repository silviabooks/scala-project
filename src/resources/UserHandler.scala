package resources

import akka.actor.{Actor, ActorLogging, Props}
//import akka.http.scaladsl.model.DateTime
import scala.collection.mutable.ArrayBuffer


object UserHandler{
  def props(): Props = {
    Props(classOf[UserHandler])
  }
}

case class User(
                 id: Long,
                 name: String,
                 telNumber: String,
                 email: String
               )

case class CreateUser(u: User)
case object GetUserList
case class GetSingleUser(id: Long)
case class UsersResponse(users: ArrayBuffer[User])
case class SingleUserResponse(user: User)

class UserHandler extends Actor with ActorLogging{

  var users : ArrayBuffer[User] = new ArrayBuffer[User]()
  var tempString: String = "User created!"

  // Initialization with random data
  var u1 : User = User(1, "Silvia", "092111", "silvia@gmail.it")
  var u2 : User = User(2, "Alessandro", "092222", "alessandro@gmail.it")
  users += (u1, u2)

  override def receive: Receive = {
    case req : CreateUser =>
      var user = req.u
      users += user
      Console.println(tempString)
      // add in DB ?
      sender() ! user
    case GetUserList =>
      log.debug("Get users list")
      sender() ! UsersResponse(users)
    case req : GetSingleUser =>
      val id: Long = req.id
      var res : User = null
      // search in users array and send the correct user
      for(u <- users) {
        if(u.id == id) {
          res = u
        }
      }
      sender() ! SingleUserResponse(res)
  }
}