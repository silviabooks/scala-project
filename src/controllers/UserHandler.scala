package controllers

import akka.actor.{Actor, ActorLogging, Props}
//import akka.http.scaladsl.model.DateTime


object UserHandler{
  def props(): Props = {
    Props(classOf[UserHandler])
  }
}

// NOTE: if new arguments added, change the 'jsonFormatX(User)' function in 'Main.scala'
case class User(
                 id: Long,
                 name: String,
                 telNumber: String,
                 email: String
               )

case class CreateUser(u: User)
case object GetUserList
case class GetSingleUser(id: Long)
case class UsersResponse(users: Array[User])
case class SingleUserResponse(user: User)
case class DeleteUser(id: Long)
case class DeleteUserResponse(user: User)

class UserHandler extends Actor with ActorLogging {

  var users : Array[User] = Array()
  var tempString: String = "User created!"

  // Initialization with random data
  var u1 : User = User(1, "Silvia", "092111", "silvia@unict.it")
  var u2 : User = User(2, "Alessandro", "092222", "alessandro@unict.it")
  users :+= u1
  users :+= u2

  override def receive: Receive = {
    case req : CreateUser =>
      var user = req.u
      users :+= user
      Console.println(tempString)
      // ToDo: add the user in the DB
      sender() ! user
    case GetUserList =>
      Console.println("Get users list")
      sender() ! UsersResponse(users)
    case req : GetSingleUser =>
      // save the request parameter in variable 'id'
      val id: Long = req.id
      var res : User = null
      // search in users array and send the correct user
      for(u <- users)
        if(u.id == id)
          res = u
      sender() ! SingleUserResponse(res)

    case req : DeleteUser =>
      // save the request parameter in variable 'id'
      val id: Long = req.id
      var res : User = null
      var indexToDelete : Long = -1
      // ToDo: delete the user from the DB
      // search in users array and delete the correct user
      for(i <- users.indices) {
        if(users(i).id == id) {
          // this is to avoid NullPointerException
          indexToDelete = i
          res = users(i)
        }
      }
      if(indexToDelete != -1) {
        // delete the user from the array (I had to use this trick because the array isn't mutable)
        val temp = users.toBuffer
        temp.remove(indexToDelete.toInt)
        users = temp.toArray
        //Console.println("Deleted user ")
        sender() ! DeleteUserResponse(res)
      } else {
        // the user with the given ID wasn't found (maybe this is redundant, but I don't care)
        sender() ! DeleteUserResponse(null)
      }
  }
}
