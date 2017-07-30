package ru.dosar.service.protocol

import java.nio.ByteBuffer

import akka.serialization.Serializer
import ru.dosar.service.protocol.WorkingDaysActor.{GetWorkingDays, Result}

class ProtocolSerializer extends Serializer {
  override final val includeManifest: Boolean = false
  override final val identifier: Int = 1000
  val GetWorkingDaysDelimiter: Byte  = 1.toByte
  val ResultDelimiter: Byte  = 2.toByte

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case GetWorkingDays(year, month) =>
      val bb = ByteBuffer.allocate(5 + month.map(_ => 4).getOrElse(0))
      bb.put(GetWorkingDaysDelimiter)
      bb.putInt(year)
      month.foreach(bb.putInt)
      bb.array()
    case Result(days) => ByteBuffer.allocate(5).put(ResultDelimiter).putInt(days).array()
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val buffer = ByteBuffer.wrap(bytes)
    buffer.get() match {
      case GetWorkingDaysDelimiter =>
        GetWorkingDays(buffer.getInt(), if(buffer.remaining() == 0) None else Some(buffer.getInt()))
      case ResultDelimiter => Result(buffer.getInt())
    }
  }
}
