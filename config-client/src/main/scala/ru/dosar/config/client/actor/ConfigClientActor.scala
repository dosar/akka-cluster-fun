package ru.dosar.config.client.actor

import akka.actor.{Actor, ActorLogging, Address, RootActorPath}
import akka.cluster.client.{ClusterClient, ClusterClientSettings}
import akka.pattern.ask
import akka.util.Timeout
import ru.dosar.service.protocol.WorkingDaysActor.{GetWorkingDays, Result}

import scala.concurrent.duration.DurationLong
import scala.util.Random

class ConfigClientActor extends Actor with ActorLogging {
  import ConfigClientActor._
  import context.dispatcher

  val client = {
    val initContact = RootActorPath(Address("akka.tcp", "dosar", "127.0.0.1", 2551)) / "system" / "receptionist"
    val props = ClusterClient.props(ClusterClientSettings(context.system).withInitialContacts(Set(initContact)))
    context.actorOf(props)
  }

  val random = new Random()

  override def receive: Receive = {
    case Start =>
      context.system.scheduler.schedule(5 seconds, 2 second, self, AskCluster)
    case AskCluster =>
      implicit val timeout = Timeout(5 seconds)
      val year = random.nextInt(10) + 2010
      val month = Some(random.nextInt(13) + 1).filter(_ < 13)
      (client ? ClusterClient.Send("/user/config-service", GetWorkingDays(year, month), true
      ))
        .recover{ case _ => Result(-100000)}
        .foreach(println)
  }
}

object ConfigClientActor {
  case object Start
  case object AskCluster
}
