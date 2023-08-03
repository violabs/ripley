package io.violabs.ripley.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest {

    object ParseAsListTest {
        @Test
        fun parseAsList_will_return_empty_list_if_null() {
            val result = null.parseAsList<String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsList_will_return_empty_list_if_not_list() {
            val result = "not a list".parseAsList<String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsList_will_return_a_list_containing_only_expected_types() {
            val result = listOf("a", 1, "b", 2).parseAsList<String>()

            assertTrue(result.size == 2)
            assertTrue(result[0] == "a")
            assertTrue(result[1] == "b")
        }

    }

    object ParseAsMapTest {
        @Test
        fun parseAsMap_will_return_empty_map_if_null() {
            val result = null.parseAsMap<String, String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMap_will_return_empty_map_if_not_map() {
            val result = "not a map".parseAsMap<String, String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMap_will_return_a_map_containing_only_expected_types() {
            val result =
                mapOf(
                    "a" to 1,
                    "b" to 2,
                    "c" to "not number",
                    4 to "not number"
                )
                    .parseAsMap<String, Int>()

            assertTrue(result.size == 2)
            assertTrue(result["a"] == 1)
            assertTrue(result["b"] == 2)
        }

    }

    object ParseAsMultiValueMapTest {

        @Test
        fun parseAsMultiValueMap_will_return_empty_map_if_null() {
            val result = null.parseAsMultiValueMap<String, String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMultiValueMap_will_return_empty_map_if_not_map() {
            val result = "not a map".parseAsMultiValueMap<String, String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMultiValueMap_will_return_a_map_containing_only_expected_types() {
            val result =
                mapOf(
                    "a" to listOf(1, 2),
                    "b" to listOf("not number", "not number"),
                    "c" to listOf(3, "not number"),
                    4 to listOf("not number", "not number")
                )
                    .parseAsMultiValueMap<String, Int>()

            assertEquals(3, result.size)
            assertEquals(listOf(1, 2), result["a"])
            assertEquals(listOf(), result["b"])
            assertEquals(listOf(3), result["c"])
        }

    }

    object ParseAsMatrixTest {
        @Test
        fun parseAsMatrix_will_return_empty_matrix_if_null() {
            val result = null.parseAsMatrix<String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMatrix_will_return_empty_matrix_if_not_list() {
            val result = "not a list".parseAsMatrix<String>()

            assertTrue(result.isEmpty())
        }

        @Test
        fun parseAsMatrix_will_return_matrix_based_on_type() {
            val result: Matrix<String> = listOf(
                listOf("a", "b"),
                listOf("c", 1)
            ).parseAsMatrix<String>()

            assertEquals(2, result.size)
            assertEquals(listOf("a", "b"), result[0])
            assertEquals(listOf("c"), result[1])
        }
    }

    object IffElseThenTest {
        @Test
        fun iff_with_then_will_evaluate_true_for_or_with_any_true() {
            var timesCalled = 1
            var completed = false

            iff(false)
                .or {
                    timesCalled++
                    true
                }
                .or {
                    timesCalled++
                    false
                }
                .then { completed = true }

            assertEquals(2, timesCalled)
            assertTrue(completed)
        }

        @Test
        fun iff_with_then_will_evaluate_false_for_or_with_all_false() {
            var timesCalled = 1
            var completed = false

            iff(false)
                .or {
                    timesCalled++
                    false
                }
                .or {
                    timesCalled++
                    false
                }
                .then { completed = true }

            assertEquals(3, timesCalled)
            assertFalse(completed)
        }

        @Test
        fun iff_with_then_will_evaluate_true_for_and_with_all_true() {
            var timesCalled = 1
            var completed = false

            iff(true)
                .and {
                    timesCalled++
                    true
                }
                .and {
                    timesCalled++
                    true
                }
                .then { completed = true }

            assertEquals(3, timesCalled)
            assertTrue(completed)
        }

        @Test
        fun iff_with_then_will_evaluate_false_for_or_with_any_false() {
            var timesCalled = 1
            var completed = false

            iff(false)
                .and {
                    timesCalled++
                    true
                }
                .and {
                    timesCalled++
                    true
                }
                .then { completed = true }

            assertEquals(1, timesCalled)
            assertFalse(completed)
        }
    }

    object IffElseReturnTest {
        private const val t = "TRUE"
        private const val f = "FALSE"

        @Test
        fun iff_with_then_will_return_first_for_or_with_any_true() {
            var timesCalled = 1

            val actual =
                iff(false)
                    .or {
                        timesCalled++
                        true
                    }
                    .or {
                        timesCalled++
                        false
                    }
                    .thenReturn(t, elseReturn = f)

            assertEquals(2, timesCalled)
            assertEquals(t, actual)
        }

        @Test
        fun iff_with_then_will_return_second_for_or_with_all_false() {
            var timesCalled = 1

            val actual =
                iff(false)
                    .or {
                        timesCalled++
                        false
                    }
                    .or {
                        timesCalled++
                        false
                    }
                    .thenReturn(t, elseReturn = f)

            assertEquals(3, timesCalled)
            assertEquals(f, actual)
        }

        @Test
        fun iff_with_then_will_return_first_for_and_with_all_true() {
            var timesCalled = 1

            val actual =
                iff(true)
                    .and {
                        timesCalled++
                        true
                    }
                    .and {
                        timesCalled++
                        true
                    }
                    .thenReturn(t, elseReturn = f)

            assertEquals(3, timesCalled)
            assertEquals(t, actual)
        }

        @Test
        fun iff_with_then_will_return_second_for_or_with_any_false() {
            var timesCalled = 1

            val actual = iff(false)
                .and {
                    timesCalled++
                    true
                }
                .and {
                    timesCalled++
                    true
                }
                .thenReturn(t, elseReturn = f)

            assertEquals(1, timesCalled)
            assertEquals(f, actual)
        }
    }
}