package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

class MissingLeakCanaryCall : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
    "${javaClass.simpleName} did not call LeakCanary.watch")

    private var isPage = false

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        if (isPage
                && klass.nameAsSafeName.toString().contains(Regex("Page$"))
                && !klass.text.contains("refWatcher.watch(this)")) {
            report(CodeSmell(issue, Entity.from(klass)))
        }
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val packageName = directive.qualifiedName
        isPage = packageName.contains("ui.page")
    }
}