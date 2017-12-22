package devTests

import akka.actor.{Actor, ActorLogging, Props}

object RequestHandler {
  def props(): Props = {
    Props(classOf[RequestHandler])
  }
}

case class Health(status: String, description: String)
case object GetHealthRequest
case class SetStatusRequest(health: Health)
case class HealthResponse(health: Health)

class RequestHandler extends Actor with ActorLogging{

  var status: Health = Health("Healthy","Initialized")

  def receive: Receive = {

    case GetHealthRequest =>
      log.debug("Received GetHealthRequest")
      sender() ! HealthResponse(status)
    case request: SetStatusRequest =>
      log.debug("Updating status to {}", request.health)
      status = request.health
      sender() ! HealthResponse(status)
  }
}
