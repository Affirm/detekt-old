package com.affirm.central.ruleset

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class AffirmRuleSetProvider(override val ruleSetId: String = "affirm-rule-set") : RuleSetProvider {
	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				MissingOnDetach()
		))
	}
}
