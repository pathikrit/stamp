package com.github.pathikrit.stamp

import java.text.DateFormatSymbols

import scala.util.Try
import scala.util.matching.Regex, Regex.{MatchIterator, Match}

package object stamp{

private[stamp] case class Rule(pattern: String, applyOnce: Boolean, matchRegexes: Seq[String], checks: List[String => Boolean] = Nil) {

  def addCheck(f: Int => Boolean) = copy(checks = checks :+ ((snippet: String) => Try(snippet.toInt) map f getOrElse false))

  def addRangeCheck(r: Range) = addCheck(r.contains)

  def apply(example: String): String = {
    val matchRegex = matchRegexes.mkString("(", ")|(", ")").r     // join them using an OR
    var count = 0
    def check(snippet: Match) = if (checks.forall(_(snippet.matched)) && (!applyOnce || (applyOnce && count == 0))) {
      count += 1
      Some(pattern)
    } else {
      None
    }
    matchRegex.replaceSomeIn(example, check)
  }
}

private[stamp] object Rule {
  implicit class StringImplicits(s: String) {
    def word = s"(?i)\\b$s\\b"
  }

  // A simple rule pertaining to numbers e.g. 'YY' must parse a number containing 2 digits
  def apply(pattern: String) = new Rule(pattern, applyOnce = true, Seq(s"\\d{${pattern.length}}"))

  // A rule pertaining to known DateFormatSymbols e.g. 'MMM' must map to 'Jan'
  def apply(pattern: String, list: DateFormatSymbols => Array[String]) = {
    import scala.collection.JavaConversions._
    new Rule(pattern, applyOnce = false, list(DateFormatSymbols.getInstance()).filter(_.nonEmpty).map(_.word))
  }

  val era = Rule("GG", _.getEras)
  val eraFull = new Rule(pattern = "GGGG", applyOnce = false,
    matchRegexes = Seq("Anno Domini".word, "Before Christ".word)) //TODO: These should be constants in Java somewhere!!

  val year2 = Rule("YY")          //TODO
  val year4 = Rule("YYYY")

  val month = Rule("M").addRangeCheck(1 to 12)      //TODO
  val month2 = Rule("MM").addRangeCheck(1 to 12)    //TODO
  val month3 = Rule("MMM", _.getShortMonths)
  val monthFull = Rule("MMMM", _.getMonths)

  val dayOfMonth = Rule("d").addRangeCheck(1 to 31)     //TODO
  val dayOfMonth2 = Rule("dd").addRangeCheck(1 to 31)   //TODO

// TODO: Q1 vs 3rd of quarter vs 03 vs 3???
//  val quarter = "QQ"
//  val fullQuarter = "QQQQ"

  val dayOfWeek3 = Rule("EEE", _.getShortWeekdays)
  val dayOfWeekFull = Rule("EEEE", _.getWeekdays)

  val amPm = Rule("a", _.getAmPmStrings)

  // TODO:
  val hourOfDayAmPm = Rule("h").addRangeCheck(1 to 12)
  val hourOfDayAmPm2 = Rule("hh").addRangeCheck(1 to 12)

  val hourOfDay = Rule("H").addRangeCheck(0 to 23)
  val hourOfDay2 = Rule("HH").addRangeCheck(0 to 23)

  val minuteOfHour = Rule("m").addRangeCheck(0 to 59)
  val minuteOfHour2 = Rule("mm").addRangeCheck(0 to 59)

  val secondOfMinute = Rule("s").addRangeCheck(0 to 59)
  val secondOfMinute2 = Rule("ss").addRangeCheck(0 to 59)

  // TODO: Support these
  /**
  S       fraction-of-second          fraction          978
   A       milli-of-day                number            1234
   n       nano-of-second              number            987654321
   N       nano-of-day                 number            1234000000

   V       time-zone ID                zone-id           America/Los_Angeles; Z; -08:30
   z       time-zone name              zone-name         Pacific Standard Time; PST
   O       localized zone-offset       offset-O          GMT+8; GMT+08:00; UTC-08:00;
   X       zone-offset 'Z' for zero    offset-X          Z; -08; -0830; -08:30; -083015; -08:30:15;
   x       zone-offset                 offset-x          +0000; -08; -0830; -08:30; -083015; -08:30:15;
   Z       zone-offset                 offset-Z          +0000; -0800; -08:00;

   '       escape for text             delimiter
   ''      single quote                literal           '
   [       optional section start
   ]       optional section end
    */
}

object FormatLike {

  private[FormatLike] implicit class StringImplicits(s: String) {
    /**
     * F#'s forward-pipe operator
     */
    def |>(rule: Rule) = rule(s)
  }

  /**
   * Given an example return a DateTimeFormatter pattern e.g.
   * DateTimeFormatter.pattern(FormatLike("Jan 26, 1996"))
   */
  def apply(example: String): String = example |>
    Rule.era |> Rule.eraFull |>
    Rule.year4 |>
    Rule.monthFull |> Rule.month3 |>
    Rule.dayOfWeekFull |> Rule.dayOfWeek3 |>
    //TODO
    Rule.dayOfMonth2
}

}
