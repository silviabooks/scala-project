package controllers


import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.StatusCodes
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import model.{User, Users}
import org.mongodb.scala.{Completed, Observer}
import org.mongodb.scala.model.Filters._
import scala.util.{Try, Success, Failure}


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

    case req : CreateUser => // Success or failure
      val requester = context.sender()
      Users().insertOne(req.u).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = requester ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = {
          println(throwable.getMessage) // TODO log
          sender ! StatusCodes.BadRequest
        }
        override def onNext(tResult: Completed): Unit = {}
      })

    case _: GetUsers => // Array[Event]
      val requester = context.sender()
      Users().find().collect().subscribe((usersList: Seq[User]) => {
        Console.println(s"$usersList")
        requester ! usersList.toArray
      })

    case req : GetUser =>
      val requester = context.sender()
      val id : Try[BsonObjectId] = Try(BsonObjectId(req.id))
      id match {
        case Success(id) =>
          Users().find(equal("_id", BsonObjectId(req.id))).collect().subscribe((results: Seq[User]) => {
            if (results.isEmpty) {
              requester ! StatusCodes.NotFound
            } else {
              requester ! results.toArray
            }
          })
        case Failure(_) =>
          requester ! StatusCodes.BadRequest
      }


    case req : DeleteUser =>
      val requester = context.sender()
      Users().deleteOne(equal("_id", BsonObjectId(req.id))).subscribe(new Observer[DeleteResult] {
        override def onComplete(): Unit = sender ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = sender ! StatusCodes.BadRequest
        override def onNext(result: DeleteResult): Unit = {}
      })

    case req : UpdateUser =>
      val requester = context.sender()
      Users().replaceOne(equal("_id", BsonObjectId(req.id)), req.u).subscribe(new Observer[UpdateResult] {
        override def onComplete(): Unit = requester ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = sender ! StatusCodes.BadRequest
        override def onNext(result: UpdateResult): Unit = {}
      })
  }
}
