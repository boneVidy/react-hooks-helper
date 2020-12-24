package gen

import com.intellij.lang.javascript.psi.*
import com.intellij.psi.PsiElement
import utils.getDependencies

fun genUseHooksCode(
    psiEle: PsiElement?,
    hooksName: String = "useMemo"
): String? {
    var code = ""
    psiEle?.let {
        when (psiEle) {
            is JSVarStatement -> {
                code = genHooksByVarStatement(psiEle, hooksName)
            }
            is JSFunction -> {
                code = genHooksByFunction(psiEle, hooksName)
            }
        }

    }
    if (code != "") {
        return code

    }
    return psiEle?.text
}

fun genHooksByVarStatement(varStatement: JSVarStatement, hooksName: String = "useMemo"): String {
    val depMap = getDependencies(varStatement)
    val varKeyword = varStatement.varKeyword?.text
    val varName = varStatement.children[1].node.firstChildNode.text
    val decalations = varStatement.children[1].node.lastChildNode.text
    var deps = ""
    if (depMap.isNotEmpty()) {
        deps = depMap.keys.reduce { acc: String, s: String -> """$acc,$s""" }
    }
    if (hooksName == "useCallback") {
        return """$varKeyword $varName = $hooksName(${decalations}, [$deps])"""
    }
    return """$varKeyword $varName = $hooksName(() => (${decalations}), [$deps])"""

}

fun genHooksByFunction(function: JSFunction, hooksName: String = "useMemo"): String {
    val depMap = getDependencies(function)
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



