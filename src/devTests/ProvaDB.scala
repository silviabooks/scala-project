package devTests

import org.bson.codecs.configuration.CodecRegistries
import org.bson.types.ObjectId
import org.mongodb.scala.bson._
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}

// Custom case classes codecs for mongo scala driver
// https://github.com/mongodb/mongo-scala-driver/blob/master/examples/src/test/scala/tour/QuickTourCaseClass.scala
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

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

  // Create a codec for the UserProva case class

  // Custom case classes: https://github.com/mongodb/mongo-scala-driver/blob/master/examples/src/test/scala/tour/QuickTourCaseClass.scala
  val codecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromProviders(classOf[UserProva]), DEFAULT_CODEC_REGISTRY)
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Document] = database.getCollection("test")
  val es: Document = Document("Lorem" -> "Ipsum", "Dolor" -> "Sit", "Amet" -> "consectetuer")

  val insertObservable: Observable[Completed] = collection.insertOne(es)
  collection.find().collect().subscribe((results: Seq[Document]) => {
    println(s"Found: ${results}")
  })

  insertObservable.subscribe(new Observer[Completed] {
    override def onNext(result: Completed): Unit = println(s"onNext: $result")
    override def onError(e: Throwable): Unit = println(s"onError: $e")
    override def onComplete(): Unit = println("onComplete")
  })


  // Custom case classes: https://github.com/mongodb/mongo-scala-driver/blob/master/examples/src/test/scala/tour/QuickTourCaseClass.scala
  val collection2: MongoCollection[UserProva] = database.getCollection("test2")
  val es2: UserProva = UserProva("asd", "asdasd", "asda@asdasd")

  val insertObservable2: Observable[Completed] = collection2.insertOne(es2)
  collection2.find().collect().subscribe((results: Seq[UserProva]) => {
    println(s"Found: ${results}")
  })

  insertObservable2.subscribe(new Observer[Completed] {
    override def onNext(result: Completed): Unit = println(s"onNext: $result")
    override def onError(e: Throwable): Unit = println(s"onError: $e")
    override def onComplete(): Unit = println("onComplete")
  })

  scala.io.StdIn.readLine()
  mongoClient.close()
  println("End")
}
