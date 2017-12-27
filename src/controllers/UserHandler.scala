package controllers


import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.StatusCodes
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import model.{User, Users}
import org.mongodb.scala.{Completed, Observer}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Filters
import scala.util.{Try, Success, Failure}


object UserHandler{
  def props(): Props = {
    Props(classOf[UserHandler])
  }
}

/**
  * Case class to match POST requests to /user and create a new [[model.User]]
  * @param u The [[model.User]] to be created.
  */
case class CreateUser(u: User)

/**
  * Case class to match GET requests to /users and get the list.
  * @param user The [[model.User]] currently logged in
  */
case class GetUsers(u : User)

/**
  * Case class to match GET requests to /users/[[id]]
  * @param id The [[model.User._id]] as [[String]] to be searched.
  */
case class GetUser(id: String)

/**
  * Case class to match DELETE requests to /users/[[id]]
  * @param id The [[model.User._id]] as [[String]] to be deleted.
  */
case class DeleteUser(id: String)

/**
  * Case class to match PUT requests to /users/[[id]]
  * @param u The [[model.User]] that performs the request.
  * @param id The [[model.User]]'s ObjectId to be searched.
  */
case class UpdateUser(u: User, id: String)

/**
  * The Controller Actor for handling /users requests
  */
class UserHandler extends Actor with ActorLogging {

  override def receive: Receive = {

    case req : CreateUser => // Success or failure
      val requester = context.sender()
      Users().insertOne(req.u).subscribe(new Observer[Completed] {
        override def onComplete(): Unit = requester ! StatusCodes.OK
        override def onError(throwable: Throwable): Unit = {
          log.error(throwable.getMessage)
          sender ! StatusCodes.BadRequest
        }
        override def onNext(tResult: Completed): Unit = {}
      })

    case r : GetUsers => // Array[Event]
      val user = r.u
      var find = Users().find(Filters.eq("_id", user._id))
      if (user.isAdmin)
        find = Users().find()
      val requester = context.sender()
      find.collect().subscribe((usersList: Seq[User]) => {
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
