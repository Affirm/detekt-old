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
            val ktFile = compileContentForTest(missingSaveCallCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(1)
        }

        it("should find one perfect file") {
            val ktFile = compileContentForTest(perfectCode)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
        }

        it("should ignore classes that aren't presenters") {
            val ktFile = compileContentForTest(notPresenter)
            subject.visit(ktFile)
            Assertions.assertThat(subject.findings).hasSize(0)
        }
    }

})

private val missingSaveCallCode: String = {
    """
			package one.ui.presenter
			class MissingOnDetachPage(private val protocolGateway: ProtocolGateway) : LinearLayout() {
                private val disposables = CompositeDisposable()

			    fun fakeCall() {
			        protocolGateway.callStuff()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { page?.setVerifyEmailTrouble(true) }
                    .subscribe({})
			    }
			}
		"""
}.invoke()

private val perfectCode: String = {
    """
			package two.ui.presenter
			class MissingOnDetachPage(private val protocolGateway: ProtocolGateway) : LinearLayout() {
                private val disposables = CompositeDisposable()

			    fun fakeCall() {
                    protocolGateway.callStuff()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { page?.setVerifyEmailTrouble(true) }
                    .subscribe()
                    .let { disposables.add(it) }
			    }
			}
		"""
}.invoke()

private val notPresenter: String = {
    """
			package one.ui.view
			class MissingOnDetachView(private val protocolGateway: ProtocolGateway) : LinearLayout() {

				override fun onAttachedToWindow() {
                    protocolGateway.callStuff()
					presenter.onAttach(this)
				}

				override fun onDetachedFromWindow() {
					presenter.onDetach()
				}
			}
		"""
}.invoke()