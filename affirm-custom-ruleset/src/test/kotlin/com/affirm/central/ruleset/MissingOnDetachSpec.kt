package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MissingOnDetachSpec : SubjectSpek<MissingOnDetach>({
	subject { MissingOnDetach() }

	describe("a simple test") {
		it("should find one file without onDetach") {
			val ktFile = compileContentForTest(missingOnDetachCode)
			subject.visit(ktFile)
			assertThat(subject.findings).hasSize(1)
		}

		it("should find one perfect file") {
			val ktFile = compileContentForTest(perfectCode)
			subject.visit(ktFile)
			assertThat(subject.findings).hasSize(0)
		}

		it("should ignore classes that aren't pages") {
			val ktFile = compileContentForTest(notAPageCode)
			subject.visit(ktFile)
			assertThat(subject.findings).hasSize(0)
		}
	}

})

private val missingOnDetachCode: String = {
	"""
			package one.ui.page
			class MissingOnDetachPage : LinearLayout() {

				override fun onAttachedToWindow() {
					presenter.onAttach(this)
				}

				override fun onDetachedFromWindow() { }
			}
		"""
}.invoke()

private val perfectCode: String = {
	"""
			package two.ui.page
			class MissingOnDetachPage : LinearLayout() {

				override fun onAttachedToWindow() {
					presenter.onAttach(this)
				}

				override fun onDetachedFromWindow() {
					presenter.onDetach()
				}
			}
		"""
}.invoke()

private val notAPageCode: String = {
	"""
			package one.ui.view
			class MissingOnDetachView : LinearLayout() {

				override fun onAttachedToWindow() {
					presenter.onAttach(this)
				}

				override fun onDetachedFromWindow() {
					presenter.onDetach()
				}
			}
		"""
}.invoke()
