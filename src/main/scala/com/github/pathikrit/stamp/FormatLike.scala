package com.github.pathikrit.stamp

import java.text.DateFormatSymbols

import scala.util.matching.Regex, Regex.Match

/**
 * Template for a rule e.g. pattern "GG" matches regex "AD, BC"
 */
class Rule(pattern: String, matchRegexes: Seq[String]) {

  val matchRegex = matchRegexes.mkString("(", ")|(", ")").r     // join them using an OR

  /**
   * Do additional sanity checks if necessary on a matched snippet
   * e.g. for "M" pattern make sure the month snippet is in between 1 and 12
   */
  def check(snippet: String) = true

  /**
   * Applies the check function to a match and returns Some(pattern) if it passes else None
   */
  private[this] def checkMatch(snippet: Match) = if (check(snippet.matched)) Some(pattern) else None

  /**
   * Replace all substrings in example which matches one of the matchRegexes and passes check with pattern
   */
  def apply(example: String): String = matchRegex.replaceSomeIn(example, checkMatch)
}

object Rule {
  import scala.collection.JavaConversions._

  private[this] implicit class StringImplicits(s: String) {
    def word = s"(?i)\\b$s\\b"
  }

  /**
   * A simple rule pertaining to numbers e.g. 'YY' must parse a number containing 2 digits
   */
  def apply(pattern: String) = new Rule(pattern, Seq(s"\\d{${pattern.length}}"))

  /**
   * A rule pertaining to known DateFormatSymbols e.g. 'MMM' must map to 'Jan'
   */
  def apply(pattern: String, list: DateFormatSymbols => Array[String]) = new Rule(pattern, list(symbols).filter(_.nonEmpty).map(_.word))

  val symbols = DateFormatSymbols.getInstance()

  val era = Rule("GG", _.getEras)
  val fullEra = new Rule(pattern = "GGGG", matchRegexes = Seq("Anno Domini".word, "Before Christ".word))

  val shortYear = Rule("YY")
  val year = Rule("YYYY")

  val month = Rule("M")
  val month2 = Rule("MM")
  val month3 = Rule("MMM", _.getShortMonths)
  val fullMonth = Rule("MMMM", _.getMonths)

  val dayOfMonth = Rule("d")
  val dayOfMonth2 = Rule("dd")
//
//  val quarter = "QQ"
//  val fullQuarter = "QQQQ"
//
    val dayOfWeek = Rule("EEE", _.getShortWeekdays)
    val fullDayOfWeek = Rule("EEEE", _.getWeekdays)
//
    val amPm = Rule("a", _.getAmPmStrings)

//
}

object FormatLike {

  private[FormatLike] implicit class StringImplicits(s: String) {
    def |>(rule: Rule) = rule(s)
  }

  /**
   * Given an example return a DateTimeFormatter pattern e.g.
   * DateTimeFormatter.pattern(FormatLike("Jan 26, 1996"))
   */
  def apply(example: String): String = example |>
    Rule.era |> Rule.fullEra |>
    Rule.year |>
    Rule.fullMonth |> Rule.month3 |> Rule.dayOfMonth
}

/**
   Q/q     quarter-of-year             number/text       3; 03; Q3; 3rd quarter

   h       clock-hour-of-am-pm (1-12)  number            12
   H       hour-of-day (0-23)          number            0
   m       minute-of-hour              number            30
   s       second-of-minute            number            55
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

   p       pad next                    pad modifier      1

   '       escape for text             delimiter
   ''      single quote                literal           '
   [       optional section start
   ]       optional section end
   #       reserved for future use
   {       reserved for future use
   }       reserved for future use

 */
