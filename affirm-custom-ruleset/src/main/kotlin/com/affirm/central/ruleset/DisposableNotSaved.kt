package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class DisposableNotSaved : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
            "${javaClass.simpleName} uses ProtocolGateway, but did not save all calls to disposables")

    // A map of disposable variable's name to whether they're saved
    private val disposableMap = mutableMapOf<String, Boolean>()

    override fun visitClass(klass: KtClass) {
        val usesProtocolGateway = klass.primaryConstructorParameters.firstOrNull {
            it.text.contains("ProtocolGateway") } != null

        if (!usesProtocolGateway) return
        super.visitClass(klass)

        disposableMap.forEach { _, used ->
            if (!used) report(CodeSmell(issue, Entity.from(klass)))
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.name != null && property.text.contains("Disposable")) {
            disposableMap.put(property.name!!, false)
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.text.split("\n").forEach {
            line ->
                disposableMap.keys.forEach {
                    if (line.contains(it) && (line.contains(".add") || line.contains("=")))
                    disposableMap[it] = true
                }
        }
    }
}