package com.github.pathikrit.stamp

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification

class FormatLikeSpec extends Specification with DataTables { override def is =
  "Work for valid examples" ! positiveSpec

  def asDateTimeFormatter(example: String) = DateTimeFormatter.ofPattern(FormatLike(example))

  val t1 = LocalDateTime.of(1986, 8, 25, 11, 23, 32, 19)

  def positiveSpec =
  "example"                                         || "time"       | "output"                                        |
  "2013 AD"                                         !! t1           ! "1986 AD"                                       |
  "1500 Anno Domini"                                !! t1           ! "1986 Anno Domini"                              |
  "Jan 26, 1926"                                    !! t1           ! "Aug 25, 1986"                                  |>
  {
    (example, time, output) => asDateTimeFormatter(example).format(time) aka FormatLike(example) mustEqual output
  }
}
