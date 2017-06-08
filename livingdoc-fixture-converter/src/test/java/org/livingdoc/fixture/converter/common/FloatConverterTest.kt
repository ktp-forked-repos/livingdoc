package org.livingdoc.fixture.converter.common

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.fixture.api.converter.ConversionException
import org.livingdoc.fixture.api.converter.Language
import org.mockito.BDDMockito.given
import utils.EnglishDefaultLocale
import java.lang.Float.MAX_VALUE
import java.lang.Float.MIN_VALUE
import java.lang.reflect.AnnotatedElement
import java.util.*

@EnglishDefaultLocale
internal class FloatConverterTest {

    val cut = FloatConverter()

    @ParameterizedTest(name = "\"{0}\" is converted to 0.0f")
    @ValueSource(strings = arrayOf("0", "0.", "0.0", "0.00", "0.000", "0.0000"))
    fun `zero values can be converted`(value: String) {
        val result = cut.convert(value)
        assertThat(result).isEqualTo(0.0f)
    }

    @Test
    fun `smallest float can be converted`() {
        val value = MIN_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MIN_VALUE)
    }

    @Test
    fun `small float can be converted`() {
        val value = "-10000000.0000001"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(-10000000.0000001f)
    }

    @Test
    fun `largest float can be converted`() {
        val value = MAX_VALUE.toString()
        val result = cut.convert(value)
        assertThat(result).isEqualTo(MAX_VALUE)
    }

    @Test
    fun `large float can be converted`() {
        val value = "10000000.0000001"
        val result = cut.convert(value)
        assertThat(result).isEqualTo(10000000.0000001f)
    }

    @Test
    fun `any float can be converted`() {
        Random().doubles(1000)//
                .map { value -> value * (MAX_VALUE - 1.0f) }//
                .forEach { value ->
                    val floatValue = value.toFloat()
                    val positiveValue = floatValue.toString()
                    val negativeValue = (-floatValue).toString()
                    assertThat(cut.convert(positiveValue)).isEqualTo(floatValue)
                    assertThat(cut.convert(negativeValue)).isEqualTo(-floatValue)
                }
    }

    @Test
    fun `leading whitespaces are removed`() {
        assertThat(cut.convert(" 1.0")).isEqualTo(1.0f)
        assertThat(cut.convert("\t1.0")).isEqualTo(1.0f)
        assertThat(cut.convert("\n1.0")).isEqualTo(1.0f)
    }

    @Test
    fun `trailing whitespaces are removed`() {
        assertThat(cut.convert("1.0 ")).isEqualTo(1.0f)
        assertThat(cut.convert("1.0\t")).isEqualTo(1.0f)
        assertThat(cut.convert("1.0\n")).isEqualTo(1.0f)
    }

    @Test
    fun `illegal format throws ConversionException`() {
        assertThrows(ConversionException::class.java) { cut.convert("hello world") }
    }

    @Nested
    inner class LanguageOverride {

        val language: Language = mock()
        val element: AnnotatedElement = mock()

        @Test
        fun `default language assumed if no element given`() {
            val result = cut.convert("42.0", null)
            assertThat(result).isEqualTo(42.0f)
        }

        @Test
        fun `default language assumed if no annotation present`() {
            given(element.getAnnotation(Language::class.java)).willReturn(null)

            val result = cut.convert("42.0", element)
            assertThat(result).isEqualTo(42.0f)
        }

        @Test
        fun `language can be overridden via annotation`() {
            given(element.getAnnotation(Language::class.java)).willReturn(language)
            given(language.value).willReturn("de")

            val result = cut.convert("42,0", element)
            assertThat(result).isEqualTo(42.0f)
        }

    }

}
