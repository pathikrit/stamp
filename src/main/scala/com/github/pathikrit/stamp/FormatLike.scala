package com.github.pathikrit.stamp

import java.time.format.DateTimeFormatter

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
 * Template for a rule e.g. pattern "GG" matches regex "AD | BC | Anno Domini | Before Christ"
 */
class Rule(pattern: String, matchRegex: Regex) {

  /**
   * Overloaded constructor that given multiple match regexes, creates a single one by ORing them
   */
  def this(pattern: String, matchRegexes: Regex*) = this(pattern, matchRegexes.mkString("(", ")|(", ")").r)

  /**
   * Do additional sanity checks if necessary on a matched snippet
   * e.g. for "M" pattern make sure the month snippet is in between 1 and 12
   */
  def check(snippet: String) = {
    println("Checking", snippet)
    true
  }

  /**
   * Applies the check function to a match and returns Some(pattern) if it passes else None
   */
  private[this] def checkMatch(snippet: Match) = if (check(snippet.matched)) Some(pattern) else None

  /**
   * Replace all substrings in example which matches one of the matchRegexes and passes check with pattern
   */
  def apply(example: String): String = {
    val result = matchRegex.replaceSomeIn(example, checkMatch)
    println("Result", result, matchRegex)
    result
  }
}

object Rule {

  private[this] implicit class StringImplicits(s: String) {
    def word = s"\\b$s\\b".r
  }

  val era = new Rule(pattern = "GG", matchRegexes = "AD".word, "BC".word)
  val fullEra = new Rule(pattern = "GGGG", matchRegexes = "Anno Domini".word, "Before Christ".word)

//
//  val shortYear   = Pattern(pattern =   "YY", example = "86")
//  val year        = Pattern(pattern = "YYYY", example = "1986")
//
//  val month       = Pattern(pattern =    "M", example = "8")
//  val month2      = Pattern(pattern =   "MM", example = "08")
//  val month3      = Pattern(pattern =  "MMM", example = "AUG")
//  val fullMonth   = Pattern"MMMM"
//
//  val dayOfMonth = "d"
//  val dayOfMonth2 = "dd"
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

  def apply(example: String): String = example |> Rule.era |> Rule.fullEra

}
