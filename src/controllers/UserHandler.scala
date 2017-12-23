package controllers

import javafx.scene.web.WebEvent

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.StatusCodes
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
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
case class GetUser(id: String)
case class DeleteUser(id: String)
case class UpdateUser(u: User, id: String)

class UserHandler extends Actor with ActorLogging {

  override def receive: Receive = {

    case req : CreateUser =>
      val sender = context.sender()
      Users().insertOne(req.u).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = sender ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = sender ! StatusCodes.BadRequest
        override def onNext(tResult: Completed): Unit = ???
      })

    case _: GetUsers =>
      Console.println("Get users list")
      Users().find().collect().subscribe((usersList: Seq[User]) => {
        Console.println(s"$usersList")
        sender ! usersList.toArray
      })

    case req : GetUser =>
      Users().find(equal("_id", BsonObjectId(req.id))).subscribe((user: User) => {
        Console.println(s"$user")
        sender ! user
      })

    case req : DeleteUser =>
      Users().deleteOne(equal("_id", BsonObjectId(req.id))).subscribe(new Observer[DeleteResult] {
        override def onComplete(): Unit = sender ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = sender ! StatusCodes.BadRequest
        override def onNext(result: DeleteResult): Unit = ???
      })

    case req : UpdateUser =>
      Users().replaceOne(equal("_id", BsonObjectId(req.id)), req.u).subscribe(new Observer[UpdateResult] {
        override def onComplete(): Unit = sender ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = sender ! StatusCodes.BadRequest
        override def onNext(result: UpdateResult): Unit = ???
      })
  }
}
