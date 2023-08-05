package io.violabs.ripley.testSuite

fun <T> testEquals(expected: T, actual: T) {
    assert(expected == actual) {
        """
            | EXPECT: $expected
            | ACTUAL: $actual
        """.trimMargin()
    }
}