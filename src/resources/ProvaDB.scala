package resources


import org.bson.types.ObjectId
import org.mongodb.scala.{Completed,
  MongoClient, MongoCollection,
  MongoDatabase, Observable,
  Observer}

import org.mongodb.scala.bson._

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
  val collection: MongoCollection[Document] = database.getCollection("test")
  val es2: Document = Document("Lorem" -> "Ipsum", "Dolor" -> "Sit", "Amet" -> "consectetuer")

  val insertObservable: Observable[Completed] = collection.insertOne(es2)

  insertObservable.subscribe(new Observer[Completed] {
    override def onNext(result: Completed): Unit = println(s"onNext: $result")
    override def onError(e: Throwable): Unit = println(s"onError: $e")
    override def onComplete(): Unit = println("onComplete")
  })

  scala.io.StdIn.readLine()
  mongoClient.close()
  println("End")
}
