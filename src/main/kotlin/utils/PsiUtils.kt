package utils

import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import consts.PsiElementTypeConst

fun isHooksStatement (varStatement: PsiElement): Boolean {
    val hooksNameList = listOf("useCallback", "useState", "useMemo")
    val callExpression = PsiTreeUtil.findChildOfType(varStatement, JSCallExpression::class.java, true)
    if (callExpression != null && callExpression.methodExpression != null) {
        if(hooksNameList.contains(callExpression.methodExpression!!.text)) {
            return true
        }
    }
    return false
}
fun getOffsetRange(element: PsiElement): Pair<Int, Int> {
    val range = element.textRange
    val startOffset: Int
    val endOffset: Int
    when (element) {
        is JSVarStatement, is JSFunction -> {
            startOffset = range.startOffset
            endOffset = range.endOffset
        }
        else -> {
            startOffset = element.context?.textRange?.startOffset ?: 0
            endOffset = element.context?.textRange?.endOffset ?: 0
        }
    }
    return Pair(startOffset, endOffset)
}

fun getDependencies(psiElement: PsiElement): MutableMap<String, MutableSet<PsiReference>> {
    return getDependencies(psiElement, null, null)
}

private fun getDependencies(
    psiElement: PsiElement,
    refMap: MutableMap<String, MutableSet<PsiReference>>?,
    ctxScope: PsiElement?
): MutableMap<String, MutableSet<PsiReference>> {
    val psiReferenceMap = refMap ?: mutableMapOf()
    val contextScope = ctxScope ?: psiElement
    if (psiElement is JSReferenceExpression) {
        if (getFunctionContext(psiElement, contextScope) == null && isValueContextInFile(psiElement, contextScope)) {
            if (psiReferenceMap[psiElement.text].isNullOrEmpty()) {
                val set = mutableSetOf<PsiReference>()
                set.add(psiElement.references[0])
                psiReferenceMap[psiElement.text] = set
            } else {
                psiReferenceMap[psiElement.text]?.add(psiElement.references[0])
            }
        }
        return psiReferenceMap
    }
    if (psiElement.children.isNotEmpty() && psiElement.elementType.toString() != PsiElementTypeConst.JS_SINGLE_TYPE) {
        psiElement.children.forEach { getDependencies(it, psiReferenceMap, contextScope) }
    }
    return psiReferenceMap
}

private fun isValueContextInFile(reference: JSReferenceExpression, context: PsiElement): Boolean {
    return reference.resolve()?.containingFile?.virtualFile?.path == context.containingFile.virtualFile.path
}


private fun getFunctionContext(psiReference: JSPsiReferenceElement, targetScope: PsiElement): JSFunction? {
    var parent = psiReference.element.parent
    val ret: JSFunctionExpression? = null
    while (parent != null) {
        if (parent is JSFunction) {
            val block = parent.block
            val varStatements =
                block?.children?.filterIsInstance<JSVarStatement>()
            var foundRef: PsiElement? = null
            if (varStatements != null) {
                for (varStatement in varStatements) {
                    for (it in varStatement.variables) {
                        if (it.name == psiReference.referenceName) {
                            foundRef = it
                        }
                    }
                }
            }
            if (parent.parameterList?.parameters != null && foundRef == null) {
                for (parameter in parent.parameterList?.parameters!!) {
                    if (parameter is JSDestructuringParameter) {
                        val jsDestructingObject: JSDestructuringObject? = PsiTreeUtil
                            .findChildOfType(parameter, JSDestructuringObject::class.java, true)

                        jsDestructingObject?.let {
                            for (destructuringParam in jsDestructingObject.children) {
                                if (destructuringParam.text == psiReference.referenceName) {
                                    foundRef = destructuringParam
                                }
                            }
                        }
                    }
                    if (parameter.name == psiReference.referenceName) {
                        foundRef = parameter
                    }
                }
            }
            if (foundRef != null) {
                return parent
            }
            if (parent == targetScope) {
                return null
            }
        }
        parent = parent.parent
    }

    return ret
}



