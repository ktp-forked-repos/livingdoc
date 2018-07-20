package org.livingdoc.example

import org.assertj.core.api.Assertions
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input

@ExecutableDocument("local://Calculator.md")
class CalculatorDocument {

    @DecisionTableFixture
    class CalculatorDecisionTableFixture {

        private lateinit var sut: Calculator

        @Input("a")
        private var valueA: Float = 0f
        private var valueB: Float = 0f

        @BeforeRow
        fun beforeRow() {
            sut = Calculator()
        }

        @Input("b")
        fun setValueB(valueB: Float) {
            this.valueB = valueB
        }

        @Check("a + b = ?")
        fun checkSum(expectedValue: Float) {
            val result = sut.sum(valueA, valueB)
            Assertions.assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a - b = ?")
        fun checkDiff(expectedValue: Float) {
            val result = sut.diff(valueA, valueB)
            Assertions.assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a * b = ?")
        fun checkMultiply(expectedValue: Float) {
            val result = sut.multiply(valueA, valueB)
            Assertions.assertThat(result).isEqualTo(expectedValue)
        }

        @Check("a / b = ?")
        fun checkDivide(expectedValue: Float) {
            val result = sut.divide(valueA, valueB)
            Assertions.assertThat(result).isEqualTo(expectedValue)
        }

    }

}