package ru.dosar.service.protocol

trait CustomMessage

object WorkingDaysActor {
  case class GetWorkingDays(year: Int, month: Option[Int]) extends CustomMessage
  case class Result(workingDays: Int) extends CustomMessage
}