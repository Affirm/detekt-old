package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory
import sun.reflect.ReflectionFactory

val PROJECT = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
		CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project.apply {
	makeMutable(this as MockProject)
}

val FACTORY = KtPsiFactory(PROJECT, false)

private fun makeMutable(project: MockProject) {
	// Based on KtLint by Shyiko
	val pomModel: PomModel = object : UserDataHolderBase(), PomModel {

		override fun runTransaction(transaction: PomTransaction) {
			(transaction as PomTransactionBase).run()
		}

		@Suppress("UNCHECKED_CAST")
		override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
			if (aspect == TreeAspect::class.java) {
				// using approach described in https://git.io/vKQTo due to the magical bytecode of TreeAspect
				// (check constructor signature and compare it to the source)
				// (org.jetbrains.kotlin:kotlin-compiler-embeddable:1.0.3)
				val constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(
						aspect, Any::class.java.getDeclaredConstructor(*arrayOfNulls<Class<*>>(0)))
				return constructor.newInstance(*emptyArray()) as T
			}
			return null
		}

	}
	val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"
	val extensionClassName = TreeCopyHandler::class.java.name!!
	arrayOf(Extensions.getArea(project), Extensions.getArea(null))
			.filter { !it.hasExtensionPoint(extensionPoint) }
			.forEach { it.registerExtensionPoint(extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE) }
	project.registerService(PomModel::class.java, pomModel)
}
