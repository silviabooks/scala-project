package model


import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

case class User(
  _id:       ObjectId,
  name:      String,
  telNumber: String,
  email:     String,
  isAdmin:   Boolean
)

object UserCreator {
  def apply(name: String, telNumber: String, email: String, isAdmin: Boolean): User =
    User(new ObjectId(), name, telNumber, email, isAdmin)
}
// singleton object that returns the users collection
object Users {
  val collection: MongoCollection[User] = BoxOffice().getCollection("users")
  def apply() : MongoCollection[User] = collection
}
