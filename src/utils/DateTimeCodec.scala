package utils

import akka.http.scaladsl.model.DateTime
import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}

class DateTimeCodec extends Codec[DateTime] {
  override def encode(bsonWriter: BsonWriter, t: DateTime, encoderContext: EncoderContext) = {
    bsonWriter.writeDateTime(t.clicks)
  }


  override def getEncoderClass = classOf[DateTime]

  override def decode(bsonReader: BsonReader, decoderContext: DecoderContext) = {
    DateTime(bsonReader.readDateTime())
  }
}
