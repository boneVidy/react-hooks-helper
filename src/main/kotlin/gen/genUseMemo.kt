package icons.gen

import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptFunctionImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import icons.consts.PsiElementTypeConst
import icons.consts.PsiElementTypeConst.Companion.JS_REFERENCE_EXPRESSION
import icons.consts.PsiElementTypeConst.Companion.JS_SINGLE_TYPE

fun genUseHooksCode(
    psiEle: PsiElement?,
    hooksName: String = "useMemo"
): String {
    var code = ""
    val psiType: IElementType? = psiEle.elementType
    psiEle?.let {
        when {
            psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT -> {
                code = genHooksByVarStatement(psiEle as JSVarStatement, hooksName)
            }
            psiType.toString() == PsiElementTypeConst.TYPESCRIPT_VARIABLE -> {
                code = genHooksByVarStatement(psiEle.context as JSVarStatement, hooksName)
            }
            psiType.toString() == PsiElementTypeConst.TYPESCRIPT_FUNCTION -> {
                code = genHooksByFunction(psiEle as TypeScriptFunctionImpl, hooksName)
            }
        }

    }
    return code
}

fun genHooksByVarStatement(varStatement: JSVarStatement, hooksName: String = "useMemo"): String {
    val psiReferenceMap = mutableMapOf<String, MutableSet<PsiReference>>()
    val depMap = getDependencies(varStatement, psiReferenceMap, varStatement)
    val varKeyword = varStatement.varKeyword?.text
    var varName = ""
    if (varStatement.variables.size == 1) {
        varName = varStatement.variables[0].name.toString()
    }
    var deps = ""
    if (depMap.isNotEmpty()) {
        deps = depMap.keys.reduce { acc: String, s: String -> """$acc,$s""" }
    }
    if (hooksName == "useCallback") {
        return """$varKeyword $varName = $hooksName(${varStatement.declarations[0].children[0].text}, [$deps])"""
    }
    return """$varKeyword $varName = $hooksName(() => ${varStatement.declarations[0].children[0].text}, [$deps])"""

}

fun genHooksByFunction(function: TypeScriptFunctionImpl, hooksName: String = "useMemo"): String {
    val psiReferenceMap = mutableMapOf<String, MutableSet<PsiReference>>()
    val depMap = getDependencies(function, psiReferenceMap, function)
    val varKeyword = "const"
    val varName = function.name
    var deps = ""
    if (depMap.isNotEmpty()) {
        deps = depMap.keys.reduce { acc: String, s: String -> """$acc,$s""" }
    }
    if (hooksName == "useCallback") {
        return """$varKeyword $varName = $hooksName(${function.text}, [$deps])"""
    }
    return """$varKeyword $varName = $hooksName(() => ${function.text}, [$deps])"""
}

private fun getDependencies(
    psiElement: PsiElement,
    psiReferenceMap: MutableMap<String, MutableSet<PsiReference>>,
    contextScope: PsiElement?
): MutableMap<String, MutableSet<PsiReference>> {
    if (psiElement.elementType.toString() == JS_REFERENCE_EXPRESSION) {
        if (psiReferenceMap[psiElement.text].isNullOrEmpty()) {
            val set = mutableSetOf<PsiReference>()
            set.add(psiElement.references[0])
            psiReferenceMap[psiElement.text] = set
        } else {
            psiReferenceMap[psiElement.text]?.add(psiElement.references[0])
        }
        return psiReferenceMap
    }
    if (psiElement.children.isNotEmpty() && psiElement.elementType.toString() != JS_SINGLE_TYPE) {
        psiElement.children.forEach { getDependencies(it, psiReferenceMap, contextScope) }
    }

    return psiReferenceMap;
}

