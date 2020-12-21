package gen

import com.intellij.lang.javascript.psi.JSDestructuringArray
import com.intellij.lang.javascript.psi.JSVarStatement

fun genUseStateByVarStatementWithDestruct(jsVarStatement: JSVarStatement): String {
    val varKeyword = jsVarStatement.varKeyword?.text
    val firstVariable = jsVarStatement.variables[0]
    val varName = if (firstVariable.context is JSDestructuringArray) {
        (firstVariable.context as JSDestructuringArray).text
    }else {
        jsVarStatement.variables[0].context?.context?.text!!
    }
    return "$varKeyword [$varName, setState] = useState(${jsVarStatement.declarations[0].initializer?.text})"
}
fun genUseStateByVarStatement(jsVarStatement: JSVarStatement): String {
    val varKeyword = jsVarStatement.varKeyword?.text
    var varName = ""
    if (jsVarStatement.variables.size == 1) {
        varName = jsVarStatement.variables[0].name.toString()
    }
    return "$varKeyword [$varName, set${varName[0].toUpperCase()}${varName.substring(1,varName.length)}] = useState(${jsVarStatement.declarations[0].children[0].text})"
}
