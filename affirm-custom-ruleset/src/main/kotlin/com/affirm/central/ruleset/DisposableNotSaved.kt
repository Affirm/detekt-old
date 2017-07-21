package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class DisposableNotSaved : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
            "${javaClass.simpleName} uses ProtocolGateway, but did not save all calls to disposables")

    // A map of disposable variable's name to whether they're saved
    private val disposableNames = mutableSetOf<String>()
    private var isPresenter = false
    override fun visitClass(klass: KtClass) {
        if (!isPresenter) return
        super.visitClass(klass)
    }

    override fun visitProperty(property: KtProperty) {
        if (property.name != null && property.text.contains("Disposable")) {
            disposableNames.add(property.name ?: "")
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val hasCall = function.text.contains("subscribeOn")
                && function.text.contains("observeOn")
                && function.text.contains("subscribe(")


        val callSaved = function.text.split("\n")
                .any {
                    line ->
                    disposableNames.any {
                        line.contains("$it = ") || line.contains("$it.add")
                    }
                }

        if (hasCall && !callSaved) {
            report(CodeSmell(issue, Entity.from(function)))
        }

        super.visitNamedFunction(function)
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val packageName = directive.qualifiedName
        isPresenter = packageName.contains("ui.presenter")
    }
}