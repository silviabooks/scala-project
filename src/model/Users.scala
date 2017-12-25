package model


import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection

/**
  * This class represents the User data model
  * @param _id the ObjectId (Bson)
  * @param name the name of the User
  * @param telNumber the telephone of the User
  * @param email the mail (used as login credential)
  * @param isAdmin a field that gives the user administration rights
  */
case class User(
  _id:       ObjectId,
  name:      String,
  telNumber: String,
  email:     String,
  isAdmin:   Boolean
)

/**
  * A factory to create a new User
  */
object UserCreator {
  def apply(name: String, telNumber: String, email: String, isAdmin: Boolean): User =
    User(new ObjectId(), name, telNumber, email, isAdmin)
}

/**
  * Singleton object that returns the users collection
  */
object Users {
  val collection: MongoCollection[User] = BoxOffice().getCollection("users")
  def apply() : MongoCollection[User] = collection
}
