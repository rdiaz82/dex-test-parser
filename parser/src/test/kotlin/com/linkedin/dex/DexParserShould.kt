package com.linkedin.dex

import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DexParserShould {
    companion object {
        val APK_PATH = "test-app/build/outputs/apk/test-app-debug-androidTest.apk"
    }

    @Test
    fun parseCorrectNumberOfTestMethods() {
        val testMethods = DexParser.findTestNames(APK_PATH)

        assertEquals(15, testMethods.size)
    }

    @Test
    fun parseMethodWithMultipleMethodAnnotations() {
        val testMethods = DexParser.findTestMethods(APK_PATH).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }

        assertEquals(2, testMethods.size)

        val method = testMethods.first()
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4")
        // TestValueAnnotation at the class level, Test annotation at the method level, and TestValueAnnotation at the method level
        assertEquals(method.annotations.size, 3)
    }

    @Test
    fun parseStringAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val classAnnotation = valueAnnotations.first()
        val stringValue = classAnnotation.values["stringValue"]
        assertNotNull(stringValue)
        assertMatches(stringValue, "Hello world!")

        val methodAnnotation = valueAnnotations[1]
        val methodStringValue = methodAnnotation.values["stringValue"]
        assertMatches(methodStringValue, "On a method")
    }

    @Test
    fun parseIntAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["intValue"]
        assertMatches(value, 12345)
    }

    @Test
    fun parseBoolAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["boolValue"]
        assertMatches(value, true)
    }

    @Test
    fun parseLongAnnotationValues() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["longValue"]
        assertMatches(value, 56789L)
    }

    @Test
    fun parseFloatAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["floatValue"]
        assertMatches(value, .25f)
    }

    @Test
    fun parseDoubleAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["doubleValue"]
        assertMatches(value, .5)
    }

    @Test
    fun parseByteAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["byteValue"]
        assertMatches(value, 0x0f.toByte())
    }

    @Test
    fun parseCharAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["charValue"]
        assertMatches(value, '?')
    }

    @Test
    fun parseShortAnnotationValues() {
        val method = getSecondBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        val value = methodAnnotation.values["shortValue"]
        assertMatches(value, 3.toShort())
    }

    @Test
    fun parseMultipleValuesInASingleAnnotation() {
        val method = getBasicJunit4TestMethod()
        val valueAnnotations = method.annotations.filter { it.name.contains("TestValueAnnotation") }

        val methodAnnotation = valueAnnotations[1]
        assertMatches(methodAnnotation.values["stringValue"], "On a method")
        assertMatches(methodAnnotation.values["intValue"], 12345)
        assertMatches(methodAnnotation.values["boolValue"], true)
        assertMatches(methodAnnotation.values["longValue"], 56789L)
    }

    private fun getBasicJunit4TestMethod(): TestMethod {
        val testMethods = DexParser.findTestMethods(APK_PATH).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }.filter { it.testName.equals("com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4") }

        assertEquals(1, testMethods.size)

        val method = testMethods.first()
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4")

        return method
    }

    private fun getSecondBasicJunit4TestMethod(): TestMethod {
        val testMethods = DexParser.findTestMethods(APK_PATH).filter { it.annotations.filter { it.name.contains("TestValueAnnotation") }.isNotEmpty() }.filter { it.testName.equals("com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4Second") }

        assertEquals(1, testMethods.size)

        val method = testMethods.first()
        assertEquals(method.testName, "com.linkedin.parser.test.junit4.java.BasicJUnit4#basicJUnit4Second")

        return method
    }

    // region value type matchers
    private fun assertMatches(value: DecodedValue?, string: String) {
        if (value is DecodedValue.DecodedString) {
            assertEquals(string, value.value)
        } else {
            throw Exception("Value was not a string type")
        }
    }

    private fun assertMatches(value: DecodedValue?, number: Int) {
        if (value is DecodedValue.DecodedInt) {
            assertEquals(number, value.value)
        } else {
            throw Exception("Value was not an int type")
        }
    }

    private fun assertMatches(value: DecodedValue?, bool: Boolean) {
        if (value is DecodedValue.DecodedBoolean) {
            assertEquals(bool, value.value)
        } else {
            throw Exception("Value was not a boolean type")
        }
    }

    private fun assertMatches(value: DecodedValue?, long: Long) {
        if (value is DecodedValue.DecodedLong) {
            assertEquals(long, value.value)
        } else {
            throw Exception("Value was not a long type")
        }
    }

    private fun assertMatches(value: DecodedValue?, float: Float) {
        if (value is DecodedValue.DecodedFloat) {
            assertEquals(float, value.value)
        } else {
            throw Exception("Value was not a float type")
        }
    }

    private fun assertMatches(value: DecodedValue?, double: Double) {
        if (value is DecodedValue.DecodedDouble) {
            assertEquals(double, value.value, 0.0)
        } else {
            throw Exception("Value was not a double type")
        }
    }

    private fun assertMatches(value: DecodedValue?, byte: Byte) {
        if (value is DecodedValue.DecodedByte) {
            assertEquals(byte, value.value)
        } else {
            throw Exception("Value was not a byte type")
        }
    }

    private fun assertMatches(value: DecodedValue?, char: Char) {
        if (value is DecodedValue.DecodedChar) {
            assertEquals(char, value.value)
        } else {
            throw Exception("Value was not a char type")
        }
    }

    private fun assertMatches(value: DecodedValue?, short: Short) {
        if (value is DecodedValue.DecodedShort) {
            assertEquals(short, value.value)
        } else {
            throw Exception("Value was not a short type")
        }
    }

    // endregion
}