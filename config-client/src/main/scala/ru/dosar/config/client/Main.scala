package ru.dosar.config.client

import akka.actor.{ActorSystem, Props}
import ru.dosar.config.client.actor.ConfigClientActor

object Main extends App {
  val system = ActorSystem("dosar-client")
  val client = system.actorOf(Props(new ConfigClientActor()))
  client ! ConfigClientActor.Start
}
