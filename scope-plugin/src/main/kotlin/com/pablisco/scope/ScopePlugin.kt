@file:Suppress("UNUSED_PARAMETER", "unused")

package com.pablisco.scope

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class ScopePlugin : Meta {
    override fun intercept(ctx: CompilerContext) =
        listOf(markScopables)
}

val Meta.markScopables: CliPlugin
    get() = "Mark Scopables" {
        meta(
            callChecker { resolvedCall, reportOn, context ->
                val bindingContext = context.trace.bindingContext
                val resultingDescriptor: CallableDescriptor = resolvedCall.resultingDescriptor

                val parentAnnotations = resolvedCall.callParentAnnotations(bindingContext).map {
                    it.fqName to it.type.annotations.hasAnnotation(ScopableName)
                }

                val callAnnotations = resultingDescriptor.annotations.map {
                    it.fqName to it.type.annotations.hasAnnotation(ScopableName)
                }

                messageCollector!!.report(
                    severity = CompilerMessageSeverity.WARNING,
                    message = "«««\n${resolvedCall.call} -> $parentAnnotations - $callAnnotations",
                    location = MessageUtil.psiElementToMessageLocation(resolvedCall.call.callElement.context)
                )

            }
        )
    }

fun ResolvedCall<*>.callParentAnnotations(bindingContext: BindingContext): Annotations =
    call.callElement.getParentOfType<KtNamedFunction>(true)
        ?.functionAnnotations(bindingContext) ?: Annotations.EMPTY

fun PsiElement.functionAnnotations(bindingContext: BindingContext): Annotations =
    bindingContext[BindingContext.FUNCTION, this]?.annotations ?: Annotations.EMPTY

fun PsiElement.annotationAnnotations(bindingContext: BindingContext): Annotations =
    bindingContext[BindingContext.CLASS, this]?.annotations ?: Annotations.EMPTY


fun Pair<SimpleFunctionDescriptor, KtFunction>.bodyContainsUnsafeCall(
    safeAnnotation: FqName,
    bindingContext: BindingContext
): Boolean {
    val (descriptor, fn) = this
    val safe = descriptor.annotations.hasAnnotation(safeAnnotation)
    if (!safe) {
        val visitor = callExpressionRecursiveVisitor {
            it.getResolvedCall(bindingContext)?.resultingDescriptor?.annotations?.hasAnnotation(safeAnnotation)
        }
        fn.accept(visitor)
    }
    return safe
}

private fun AnnotationDescriptor.hasScropableAnnotation() =
    type.annotations.any { it.isScropable() }

private fun AnnotationDescriptor.isScropable() = fqName == ScopableName

private val ScopableName = FqName("com.pablisco.scope.api.Scopable")
