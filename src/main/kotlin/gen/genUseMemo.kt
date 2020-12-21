package gen

import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.JSVariable
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
            is JSVariable -> {
                code = genHooksByVarStatement(psiEle.context as JSVarStatement, hooksName)
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



