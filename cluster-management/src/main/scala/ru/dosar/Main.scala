package ru.dosar

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement

object Main extends App {
  val system = ActorSystem("dosar")
  val cluster = Cluster(system)
  ClusterHttpManagement(cluster).start()
}
