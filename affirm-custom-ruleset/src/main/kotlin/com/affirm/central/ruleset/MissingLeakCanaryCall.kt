package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPackageDirective

class MissingLeakCanaryCall : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
    "${javaClass.simpleName} did not call LeakCanary.watch")

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (!klass.text.contains("refWatcher.watch(this)")) {
            report(CodeSmell(issue, Entity.from(klass)))
        }
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        if (!directive.qualifiedName.contains("ui.page")) {
            return
        }

        super.visitPackageDirective(directive)
    }
}