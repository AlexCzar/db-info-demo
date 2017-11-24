package io.czar.dbinfodemo.utils


import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Regex matcher copied form official
 * [hamcrest repo](https://github.com/hamcrest/JavaHamcrest/blob/425f095bf363f8cedbc9d00fd116c8bb89d01c26/hamcrest-core/src/main/java/org/hamcrest/core/CombinableMatcher.java)
 */
class MatchesPattern(private val pattern: Regex) : TypeSafeMatcher<String>() {

	override fun matchesSafely(item: String): Boolean = pattern.matches(item)

	override fun describeTo(description: Description) {
		description.appendText("a string matching the pattern '$pattern'")
	}

	companion object {

		/**
		 * Creates a matcher of [java.lang.String] that matches when the examined string
		 * exactly matches the given [java.util.regex.Pattern].
		 */
		fun matchesPattern(pattern: Regex): Matcher<String> = MatchesPattern(pattern)

		/**
		 * Creates a matcher of [java.lang.String] that matches when the examined string
		 * exactly matches the given regular expression, treated as a [java.util.regex.Pattern].
		 */
		fun matchesPattern(regex: String): Matcher<String> = MatchesPattern(Regex(regex))
	}
}
