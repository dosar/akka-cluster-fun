package ru.dosar

import akka.actor.{ActorSystem, Props}
import akka.cluster.client.ClusterClientReceptionist
import ru.dosar.actor.WorkingDaysActor

object Main extends App {

  val system = ActorSystem("dosar")
  val actor = system.actorOf(Props(new WorkingDaysActor()), "config-service")
  ClusterClientReceptionist(system).registerService(actor)
}
