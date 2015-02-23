stamp [![Build Status](https://travis-ci.org/pathikrit/stamp.png?branch=master)](http://travis-ci.org/pathikrit/stamp)
--------

Because life is too short to grok [Java's DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html):


```scala
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.github.pathikrit.stamp._

val pattern: String = FormatLike("Feb 05, 2014")
assert(pattern == "MMM DD, YYYY")

val formatter = DateTimeFormatter.ofPattern(pattern)
println(LocalDateTime.now().format(formatter))
```

[See the tests for more examples](src/test/scala/com/github/pathikrit/stamp/FormatLikeSpec.scala)
