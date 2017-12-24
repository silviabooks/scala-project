package model

import akka.http.scaladsl.model.DateTime
import org.bson.codecs.configuration.CodecRegistries
import org.mongodb.scala.{MongoClient, MongoDatabase}
import utils.DateTimeCodec

// Custom case classes codecs for mongo scala driver
// https://github.com/mongodb/mongo-scala-driver/blob/master/examples/src/test/scala/tour/QuickTourCaseClass.scala
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY


// Singleton class to be used privately in this package to get one only connection to the MongoDB
// This could be a good solution (mantain only one connection) because of weak consistency model needed
// We're using a NoSql Database
object BoxOffice {
  // TODO put here the others case classes to be included in the codecRegistry
  val codecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new DateTimeCodec()), CodecRegistries.fromProviders(classOf[Event], classOf[User], classOf[Ticket]), DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient()
  // Here we should use some "constant" or application property to call elsewhere if needed and to be not hardcoded
  val database: MongoDatabase = mongoClient.getDatabase("boxoffice").withCodecRegistry(codecRegistry)

  def apply() : MongoDatabase = database
}
