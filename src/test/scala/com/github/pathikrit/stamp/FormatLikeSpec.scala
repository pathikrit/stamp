package com.github.pathikrit.stamp

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

import org.specs2.mutable.Specification

class FormatLikeSpec extends Specification {

  private[this] implicit class StringImplicits(s: String) {
    def asDateTimeFormatter = DateTimeFormatter.ofPattern(FormatLike(s))

    def mustFormatTo(output: String)(implicit t: TemporalAccessor) = {
      s.asDateTimeFormatter.format(t) aka FormatLike(s) mustEqual output
    }
  }

  "stamp" should {
    "parse human examples" in {
      implicit val date = LocalDateTime.of(1986, 8, 25, 11, 23, 32, 19)
      "2013 AD" mustFormatTo "1986 AD"
      "Jan 26, 1926" mustFormatTo "Aug 25, 1986"
    }
  }
}
