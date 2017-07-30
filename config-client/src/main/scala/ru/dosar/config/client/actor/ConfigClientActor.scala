package ru.dosar.config.client.actor

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Address, RootActorPath}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import akka.pattern.ask
import akka.routing.{RoundRobinPool, Router}
import akka.util.Timeout
import ru.dosar.service.protocol.WorkingDaysActor.{GetWorkingDays, Result}

import scala.concurrent.duration.DurationLong
import scala.util.Random

class ConfigClientActor extends Actor with ActorLogging {
  import ConfigClientActor._
  import context.dispatcher

  val maxRequests = 5
  var successfull = 0
  var failed = 0
  var timestamp = 0l
  val poolSize = 3

  val clients = {
    val ports = 2551 to 2553
    for(i <- 0 until poolSize) yield {
      val props = ClusterClient.props(ClusterClientSettings(context.system)
        .withInitialContacts(Set(
          RootActorPath(Address("akka.tcp", "dosar", "127.0.0.1", ports(i % ports.size))) / "system" / "receptionist",
        )))
      context.actorOf(props)
    }
  }

  val random = new Random()

  override def receive: Receive = {
    case Success =>
      successfull += 1
      printResults()
    case Failure =>
      failed += 1
      printResults()
    case Start =>
      context.system.scheduler.scheduleOnce(2 seconds, self, AskCluster)
    case AskCluster =>
      println("Started")
      implicit val timeout = Timeout(10 seconds)
      timestamp = Instant.now().toEpochMilli
      for (i <- 1 to maxRequests) {
        val year = random.nextInt(10) + 2010
        val month = Some(random.nextInt(13) + 1).filter(_ < 13)
        (context.actorSelection("akka.tcp://127.0.0.1:2551/user/config-service") ? GetWorkingDays(year, month))
          .onComplete(x => if(x.isSuccess) self ! Success else self ! Failure)

        //        (clients(i % clients.size) ? ClusterClient.Send("/user/config-service", GetWorkingDays(year, month), true))
//          .onComplete(x => if(x.isSuccess) self ! Success else self ! Failure)
      }
  }

  def printResults() =
    if(successfull + failed == maxRequests) {
      println("==============================")
      println(s"time is ${Instant.now().toEpochMilli - timestamp}ms success/failure is $successfull/$failed")
      println("==============================")
    }
}

object ConfigClientActor {
  case object Start
  case object AskCluster
  case object Success
  case object Failure
  case class AskError(exc: Throwable)
}
