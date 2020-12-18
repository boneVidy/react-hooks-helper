package gen

import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import consts.PsiElementTypeConst

fun genUseStateCode (
    psiEle: PsiElement?
): String {
    var code = ""
    val psiType: IElementType? = psiEle.elementType
    psiEle?.let {
        when {
            psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT -> {
                code = genUseStateByVarStatement(psiEle as JSVarStatement)
            }
            psiType.toString() == PsiElementTypeConst.TYPESCRIPT_VARIABLE -> {
                code = genUseStateByVarStatement(psiEle.context as JSVarStatement)
            }
        }

    }
    return code
}

fun genUseStateByVarStatement(jsVarStatement: JSVarStatement): String {
    val varKeyword = jsVarStatement.varKeyword?.text
    var varName = ""
    if (jsVarStatement.variables.size == 1) {
        varName = jsVarStatement.variables[0].name.toString()
    }
    return "$varKeyword [$varName, set${varName[0].toUpperCase()}${varName.substring(1,varName.length)}] = useState(${jsVarStatement.declarations[0].children[0].text})"
}
