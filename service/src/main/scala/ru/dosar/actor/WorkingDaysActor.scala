package ru.dosar.actor

import java.time.{LocalDate, Year, YearMonth}

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import ru.dosar.service.protocol.WorkingDaysActor.{GetWorkingDays, Result}

class WorkingDaysActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def receive: Receive = clusterMembership orElse {

    case gwd @ GetWorkingDays(year, Some(month)) =>
//      log.info("got {}", gwd)
      val monthOfYear = YearMonth.of(year, month)
      val result = (1 to monthOfYear.lengthOfMonth())
        .count(day => LocalDate.of(year, month, day).getDayOfWeek.getValue < 6)
      sender() ! Result(result)
    case gwd @ GetWorkingDays(year, None) =>
//      log.info("got {}", gwd)
      val startDate = LocalDate.of(year, 1, 1)
      val result = (0 until Year.of(year).length())
        .foldLeft(0){ case (count, days) =>
            val day = startDate.plusDays(days)
            if(day.getDayOfWeek.getValue < 6) count + 1
            else count
        }
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