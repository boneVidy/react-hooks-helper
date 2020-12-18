package gen

import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptFunctionImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import consts.PsiElementTypeConst
import utils.getDependencies

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

fun genHooksByFunction(function: TypeScriptFunctionImpl, hooksName: String = "useMemo"): String {
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



