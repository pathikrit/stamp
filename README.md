stamp [![Circle CI](https://img.shields.io/circleci/project/pathikrit/stamp.svg)](https://circleci.com/gh/pathikrit/stamp) [![Download](https://api.bintray.com/packages/pathikrit/maven/stamp/images/download.svg)](https://bintray.com/pathikrit/maven/stamp/_latestVersion)
--------

Because life is too short to grok [Java's DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html):
```scala
import com.github.pathikrit.stamp._

val pattern: String = FormatLike("Feb 05, 2014")
assert(pattern == "MMM DD, YYYY")
```

You can then use the pattern as:
```scala
import java.time.LocalDateTime, java.time.format.DateTimeFormatter

val formatter = DateTimeFormatter.ofPattern(pattern)
println(LocalDateTime.now().format(formatter))
```

[See the tests for more examples](src/test/scala/com/github/pathikrit/stamp/FormatLikeSpec.scala)


See also: [ScalaVerbalExpressions](https://github.com/pathikrit/ScalaVerbalExpressions) - another library for writing self-documenting regexes.
