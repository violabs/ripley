package io.violabs.ripley.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilsTest {

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