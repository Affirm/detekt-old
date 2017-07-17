package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MissingLeakCanaryCallSpec : SubjectSpek<MissingOnDetach>({
    subject { MissingOnDetach() }

    describe("a simple test") {
        it("should find one file that didn't call LeakCanary") {
            val ktFile = compileContentForTest(missingOnDetachCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(1)
        }

        it("should find one perfect file") {
            val ktFile = compileContentForTest(perfectCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
        }

        it("should ignore classes that aren't pages") {
            val ktFile = compileContentForTest(notAPageCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
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

				override fun onDetachedFromWindow() {
				}
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
                    (context.applicationContext as CentralApplication).refWatcher.watch(this)
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