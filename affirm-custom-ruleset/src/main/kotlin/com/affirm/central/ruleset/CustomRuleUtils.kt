package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtPsiUtil

val EXCLUDE_ANNOTATION_NAME = "DetektIgnore"

fun Rule.isExcluded(annotationEntries: List<KtAnnotationEntry>): Boolean {
    return annotationEntries.firstOrNull {
        KtPsiUtil.getShortName(it).toString() == EXCLUDE_ANNOTATION_NAME
        && it.text.contains(javaClass.simpleName)
    } != null
}