package ru.dosar

import akka.actor.{ActorSystem, Props}
import ru.dosar.actor.WorkingDaysActor

object Main extends App {

  val system = ActorSystem()
  val actor = system.actorOf(Props(new WorkingDaysActor()))
}
