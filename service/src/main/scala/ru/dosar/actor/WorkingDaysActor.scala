package ru.dosar.actor

import java.time.{LocalDate, YearMonth}

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import ru.dosar.actor.WorkingDaysActor.{GetWorkingDays, Result}

class WorkingDaysActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive: Receive = clusterMembership orElse {

    case GetWorkingDays(year, Some(month)) =>
      val monthOfYear = new YearMonth(year, month)
      val result = (1 to monthOfYear.lengthOfMonth())
        .count(day => new LocalDate(year, month, day).getDayOfWeek.getValue < 6)
      sender() ! Result(result)
  }

  private def clusterMembership: Receive = {

    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case e: MemberEvent => log.info("got member event '{}'", e)
  }
}

object WorkingDaysActor {
  case class GetWorkingDays(year: Int, month: Option[Int])
  case class Result(workingDays: Int)
}