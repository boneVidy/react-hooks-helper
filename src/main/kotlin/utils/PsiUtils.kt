package icons.utils

import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import icons.consts.PsiElementTypeConst

fun getOffsetRange(element: PsiElement): Pair<Int, Int> {
    val range = element.textRange
    val psiType: IElementType? = element.elementType
    val startOffset: Int
    val endOffset: Int
    when {
        psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT || psiType.toString() == PsiElementTypeConst.TYPESCRIPT_FUNCTION -> {
            startOffset = range.startOffset
            endOffset = range.endOffset
        }
        else -> {
            startOffset = element.context?.textRange?.startOffset ?: 0
            endOffset = element.context?.textRange?.endOffset ?: 0
        }
    }
    return Pair( startOffset, endOffset)
}
fun getDependencies(psiElement: PsiElement): MutableMap<String, MutableSet<PsiReference>> {
    return getDependencies(psiElement, null, null, null)
}

private fun getDependencies(
    psiElement: PsiElement,
    refMap: MutableMap<String, MutableSet<PsiReference>>?,
    ctxScope: PsiElement?,
    varMap: MutableMap<String, MutableSet<JSVariable>>?
): MutableMap<String, MutableSet<PsiReference>> {
    val psiReferenceMap = refMap ?: mutableMapOf()
    val contextScope = ctxScope ?: psiElement
    val innerVarMap = varMap ?: mutableMapOf()
    if (psiElement.elementType.toString() == PsiElementTypeConst.JS_REFERENCE_EXPRESSION) {
        if (psiReferenceMap[psiElement.text].isNullOrEmpty()) {
            val set = mutableSetOf<PsiReference>()
            set.add(psiElement.references[0])
            psiReferenceMap[psiElement.text] = set
        } else {
            psiReferenceMap[psiElement.text]?.add(psiElement.references[0])
        }
    }
    if (psiElement.elementType .toString() == PsiElementTypeConst.JS_VAR_STATEMENT) {
        (psiElement as JSVarStatement).variables.forEach {
            val name = it.name
            if (name != null) {
                if (innerVarMap[name].isNullOrEmpty()) {
                    val set = mutableSetOf<JSVariable>()
                    set.add(it)
                    innerVarMap[name] = set
                }else {
                    innerVarMap[name]?.add(it)
                }
            }

        }
    }
    if (psiElement.children.isNotEmpty() && psiElement.elementType.toString() != PsiElementTypeConst.JS_SINGLE_TYPE) {
        psiElement.children.forEach { getDependencies(it, psiReferenceMap, contextScope, innerVarMap) }
    }
    println(innerVarMap)
//    innerVarMap.forEach { }
//    psiReferenceMap.forEach { if (it.name ==)}
    return psiReferenceMap;
}