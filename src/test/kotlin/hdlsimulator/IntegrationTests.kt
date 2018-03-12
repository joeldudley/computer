package hdlsimulator

import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val evaluator = HdlSimulator()

    @Test
    fun notTest() {
        evaluator.loadChip("Not")

        evaluator.evaluateChip(listOf("in" to false))
        assertEquals(true, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("in" to true))
        assertEquals(false, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("in" to false))
        assertEquals(true, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("in" to true))
        assertEquals(false, evaluator.readValue("out"))
    }

    @Test
    fun andTest() {
        evaluator.loadChip("And")

        evaluator.evaluateChip(listOf("a" to false, "b" to false))
        assertEquals(false, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to false, "b" to true))
        assertEquals(false, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to true, "b" to false))
        assertEquals(false, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to true, "b" to true))
        assertEquals(true, evaluator.readValue("out"))
    }

    @Test
    fun orTest() {
        evaluator.loadChip("Or")

        evaluator.evaluateChip(listOf("a" to false, "b" to false))
        assertEquals(false, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to false, "b" to true))
        assertEquals(true, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to true, "b" to false))
        assertEquals(true, evaluator.readValue("out"))

        evaluator.evaluateChip(listOf("a" to true, "b" to true))
        assertEquals(true, evaluator.readValue("out"))
    }
}