package gen

import com.intellij.ide.projectView.ProjectView
import com.intellij.lang.javascript.psi.JSDestructuringArray
import com.intellij.lang.javascript.psi.JSDestructuringProperty
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.reWriteCode
import utils.getOffsetRange

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

fun genUseStateCode (editor: Editor?, project: Project?, element: PsiElement) {
    val document = editor?.document
    var parent = element.parent
    var code = ""
    parent?.let {
        when (parent) {
            is JSVarStatement -> {
                code = genUseStateByVarStatement(parent as JSVarStatement)
            }
            is JSVariable -> {
                code = if (parent.context is JSDestructuringProperty || parent.context is JSDestructuringArray) {
                    parent = PsiTreeUtil.getContextOfType(parent, JSVarStatement::class.java, true)
                    genUseStateByVarStatementWithDestruct(parent as JSVarStatement)
                } else {
                    genUseStateByVarStatement(parent.context as JSVarStatement)
                }

            }
        }
        val pair = getOffsetRange(parent)
        val startOffset: Int = pair.first
        val endOffset: Int = pair.second
        reWriteCode(project, document, code, startOffset, endOffset)
        ProjectView.getInstance(project).refresh()
    }
}