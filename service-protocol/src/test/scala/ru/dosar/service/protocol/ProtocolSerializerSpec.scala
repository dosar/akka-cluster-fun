package ru.dosar.service.protocol

import org.scalatest.{Matchers, WordSpec}
import ru.dosar.service.protocol.WorkingDaysActor.GetWorkingDays

class ProtocolSerializerSpec extends WordSpec with Matchers {
  "ProtocolSerializer" should {
    "serialize" in {
      val getWorkingDays = GetWorkingDays(2000, Some(10))
      val serializer = new ProtocolSerializer()
      val serialized = serializer.toBinary(getWorkingDays)
      serializer.fromBinary(serialized) shouldBe getWorkingDays
    }
  }
}