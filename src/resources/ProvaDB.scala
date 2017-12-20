package resources


import org.bson.types.ObjectId
import org.mongodb.scala.{Completed,
  MongoClient, MongoCollection,
  MongoDatabase, Observable,
  Observer}

// case class and companion object
object UserProva {
  def apply(name: String, telNumber: String, email: String): UserProva =
    UserProva(new ObjectId(), name, telNumber, email)
}

case class UserProva(
                      _id: ObjectId,
                      name: String,
                      telNumber: String,
                      email: String
                    )

object ProvaDB extends App {
  println("Start")
  // default connection on
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[UserProva] = database.getCollection("test")
  val esempio: UserProva = UserProva("silvia", "111", "silvia@io.it")
  val observable: Observable[Completed] = collection.insertOne(esempio)
  // This doesn't works!
  observable.subscribe(new Observer[Completed] {
    override def onNext(result: Completed): Unit = println("Inserted")
    override def onError(e: Throwable): Unit = println("Failed")
    override def onComplete(): Unit = println("Completed")
  })

  scala.io.StdIn.readLine()
  mongoClient.close()
  println("End")
}
