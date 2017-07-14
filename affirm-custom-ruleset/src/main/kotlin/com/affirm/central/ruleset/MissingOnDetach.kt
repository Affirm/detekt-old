package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MissingOnDetach : Rule() {

	override val issue = Issue(javaClass.simpleName, Severity.CodeSmell,
			"${javaClass.simpleName} called onAttach but didn't call onDetach")

	override fun visitClass(klass: KtClass) {
		super.visitClass(klass)

		if (!klass.nameAsSafeName.toString().contains("Page")) {
			return
		}

		if (klass.text.contains("onAttach(this)") && !klass.text.contains("onDetach()")) {
			report(CodeSmell(issue, Entity.from(klass)))
		}
	}
}
