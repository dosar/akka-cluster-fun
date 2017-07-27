package ru.dosar.service.protocol

trait ProtoMessage

object WorkingDaysActor {
  case class GetWorkingDays(year: Int, month: Option[Int])// extends ProtoMessage
  case class Result(workingDays: Int)// extends ProtoMessage
}