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

  val symbols = DateFormatSymbols.getInstance()
  def toWords(a: Array[String]) = a.filter(_.nonEmpty).map(_.word)

  val era = new Rule(pattern = "GG", matchRegexes = toWords(symbols.getEras))
  val fullEra = new Rule(pattern = "GGGG", matchRegexes = Seq("Anno Domini".word, "Before Christ".word))

  val shortYear = Rule("YY")
  val year = Rule("YYYY")

  val month = Rule("M")
  val month2 = Rule("MM")
  val month3 = new Rule(pattern = "MMM", matchRegexes = toWords(symbols.getShortMonths))
  val fullMonth = new Rule(pattern = "MMM", matchRegexes = toWords(symbols.getMonths))

  val dayOfMonth = Rule("d")
  val dayOfMonth2 = Rule("dd")
//
//  val quarter = "QQ"
//  val fullQuarter = "QQQQ"
//
//  val dayOfWeek = "EEE"
//  val fullDayOfWeek = "EEEE"
//
//  val amPm = "a"
//  val fullAmPm = "a" * 2
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
