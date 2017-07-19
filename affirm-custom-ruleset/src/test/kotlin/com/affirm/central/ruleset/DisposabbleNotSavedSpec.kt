package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class DisposabbleNotSavedSpec : SubjectSpek<DisposableNotSaved>({
    subject { DisposableNotSaved() }

    describe("a simple test") {
        it("should find one file that didn't save all calls to disposable") {
            val ktFile = compileContentForTest(missingOnDetachCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(1)
        }

        it("should find one perfect file") {
            val ktFile = compileContentForTest(perfectCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
        }

        it("should ignore classes that don't use protocolGateway") {
            val ktFile = compileContentForTest(notAPageCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
        }
    }

})

private val missingOnDetachCode: String = {
    """
			package one.ui.presenter
			class MissingOnDetachPage(private val protocolGateway: protocolGateway) : LinearLayout() {
                private val disposables = CompositeDisposable()
                private val disposable: CompositeDisposable? = null

				override fun onAttach() {
					presenter.onAttach(this)
				}

				override fun onDetach() {
                    disposable.add(this)
				}
			}
		"""
}.invoke()

private val perfectCode: String = {
    """
			package two.ui.page
			class MissingOnDetachPage(private val protocolGateway: protocolGateway) : LinearLayout() {
                private val disposables = CompositeDisposable()
                private val disposable: CompositeDisposable? = null

				override fun onAttach() {
					presenter.onAttach(this)
				}

				override fun onDetache() {
                   disposables.add()
                   disposable = blah
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