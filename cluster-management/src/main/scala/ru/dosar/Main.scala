package ru.dosar

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement

class Main extends App {
  val system = ActorSystem()
  val cluster = Cluster(system)
  ClusterHttpManagement(cluster).start()
}
