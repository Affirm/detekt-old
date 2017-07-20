package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class DisposableNotSaved : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
            "${javaClass.simpleName} uses ProtocolGateway, but did not save all calls to disposables")

    // A map of disposable variable's name to whether they're saved
    private var disposableName: String? = null
    private var protocolGatewayVarName: String? = null
    private var isPresenter = false
    override fun visitClass(klass: KtClass) {
        if (!isPresenter) return

        if (protocolGatewayVarName == null) {
            protocolGatewayVarName = klass.primaryConstructorParameters.firstOrNull {
                it.text.contains("ProtocolGateway") }?.name
        }

        if (protocolGatewayVarName == null || protocolGatewayVarName!!.isEmpty()) return
        super.visitClass(klass)
    }

    override fun visitProperty(property: KtProperty) {
        if (property.name != null && property.text.contains("CompositeDisposable")) {
            disposableName = property.name
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {

        val hasCall = function.text.count(protocolGatewayVarName) > 0
        val subscribed = function.text.contains(".subscribe")
        val callSaved = function.text.contains(disposableName + ".add")

        if (hasCall && subscribed && !callSaved) {
            report(CodeSmell(issue, Entity.from(function)))
        }
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val packageName = directive.qualifiedName
        isPresenter = packageName.contains("ui.presenter")
    }
}