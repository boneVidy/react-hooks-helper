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

fun genUseMemoCode(
    psiEle: PsiElement?
): String {
    var code = ""
    val psiType: IElementType? = psiEle.elementType
    psiEle?.let {
        when {
            psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT -> {
                code = genUseMemoByVarStatement(psiEle as JSVarStatement)
            }
            psiType.toString() == PsiElementTypeConst.TYPESCRIPT_VARIABLE -> {
                code = genUseMemoByVarStatement(psiEle.context as JSVarStatement)
            }
            psiType.toString() == PsiElementTypeConst.TYPESCRIPT_FUNCTION -> {
                code = genUseMemoByFunction(psiEle as TypeScriptFunctionImpl)
            }
        }

    }
    return code
}

fun genUseMemoByVarStatement(varStatement: JSVarStatement): String {
    val psiReferenceMap = mutableMapOf<String, MutableSet<PsiReference>>()
    val depMap = getDependencies(varStatement, psiReferenceMap, varStatement)
    val varKeyword = varStatement.varKeyword?.text
    var varName = ""
    if (varStatement.variables.size == 1) {
        varName = varStatement.variables[0].name.toString()
    }
    val deps = depMap.keys.reduce { acc: String, s: String -> """$acc,$s""" }
    return """$varKeyword $varName = useMemo(() => ${varStatement.declarations[0].children[0].text}, [$deps])"""

}

fun genUseMemoByFunction(function: TypeScriptFunctionImpl): String {
    val psiReferenceMap = mutableMapOf<String, MutableSet<PsiReference>>()
    val depMap = getDependencies(function, psiReferenceMap, function)
    val varKeyword = "const"
    val varName = function.name
    var deps = ""
    if (depMap.isNotEmpty()) {
        deps = depMap.keys.reduce { acc: String, s: String -> """$acc,$s""" }
    }
    return """$varKeyword $varName = useMemo(() => ${function.text}, [$deps])"""
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

fun genUseMemoByFunction(): String {
    val ret = ""

    return ret
}
