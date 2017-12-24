package utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.DateTime
import devTests.Health
import model.{Event, Ticket, User}
import org.mongodb.scala.bson.ObjectId
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.util.Try

trait JsonMarshalling extends SprayJsonSupport with DefaultJsonProtocol {

  // Conversion of DateTime in Json format, see:
  // https://stackoverflow.com/questions/25178108/converting-datetime-to-a-json-string
  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    override def write(obj: DateTime) = JsString(obj.toIsoDateTimeString())

    override def read(json: JsValue): DateTime = {
      val x = DateTime.fromIsoDateTimeString(json.convertTo[String])
      x match {
        case Some(d) => d
        case None => throw new DeserializationException("Error on date deserialization")
      }
    }
  }

  implicit object ObjectIdFormat extends RootJsonFormat[ObjectId] {
    override def write(obj: ObjectId) = JsString(obj.toString())

    override def read(json: JsValue): ObjectId = {
      Try(new ObjectId(json.convertTo[String])) match {
        case scala.util.Success(oid) => oid
        case scala.util.Failure(_) => new ObjectId()
      }
    }
  }
  // implicit variables needed to the marshalling/unmarshalling of the case classes
  implicit val eventFormat  = jsonFormat7(Event)
  implicit val userFormat   = jsonFormat5(User)
  implicit val healthFormat = jsonFormat2(Health)
  implicit val ticketFormat = jsonFormat4(Ticket)

}