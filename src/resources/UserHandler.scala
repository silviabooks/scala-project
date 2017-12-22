package resources

import akka.actor.{Actor, ActorLogging, Props}
import org.mongodb.scala.result.DeleteResult
//import akka.http.scaladsl.model.DateTime
import model.{User, Users}
import org.mongodb.scala.{Completed, Observer}
import org.mongodb.scala.model.Filters._



object UserHandler{
  def props(): Props = {
    Props(classOf[UserHandler])
  }
}

case class CreateUser(u: User)
case class GetUsers()
case class GetUser(id: Any) // use ObjectId if this doesn't work with the API
case class DeleteUser(id: Any)
case class UpdateUser(id: Any, u: User)
//case class UsersResponse(users: Array[User])
//case class SingleUserResponse(user: User)
case class DeleteUserResponse(resp: Any)


class UserHandler extends Actor with ActorLogging {

  override def receive: Receive = {

    case req : CreateUser =>
      val sender = context.sender()
      Users().insertOne(req.u).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = sender ! req.u
        override def onError(throwable: Throwable): Unit = ???
        override def onNext(tResult: Completed): Unit = ???
      })

    case _: GetUsers =>
      Console.println("Get users list")
      Users().find().collect().subscribe((usersList: Seq[User]) => {
        Console.println(s"$usersList")
        sender ! usersList.toArray
      })

    case req : GetUser =>
      Users().find(equal("_id", req.id)).subscribe((user: User) => {
        Console.println(s"$user")
        sender ! user
      })

    case req : DeleteUser =>
      Users().deleteOne(equal("_id", req.id)).subscribe(new Observer[DeleteResult] {
        override def onComplete(): Unit = sender ! req.id
        override def onError(throwable: Throwable): Unit = ???
        override def onNext(result: DeleteResult): Unit = ???
      })

  }
}