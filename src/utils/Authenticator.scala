package utils

import akka.http.scaladsl.server.directives.Credentials
import model.{User, Users}
import org.mongodb.scala.model.Filters

import scala.concurrent.Future

/**
  * Singleton to provide Basic HTTP authentication (No password given, it only
  * checks for the existence of the given email in the [[model.Users]]
  * collection.
  */
object Authenticator  {
 import ActorInitializer._
 def adminPassAuthenticator(credentials: Credentials) : Future[Option[User]] =
   credentials match {
     case Credentials.Provided(id) =>
       Users().find(Filters.eq("email", id)).collect().toFuture().map[Option[User]](_.headOption)
     case _ => Future.successful(None)
   }
}
