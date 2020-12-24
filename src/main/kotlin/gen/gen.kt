package gen

import com.intellij.ide.projectView.ProjectView
import com.intellij.lang.javascript.psi.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.reWriteCode
import utils.getOffsetRange


fun genUseMemoOrUseCallbackCode(editor: Editor?, project: Project?, element: PsiElement?, hooksName: String) {
    val document = editor?.document
    var code: String? = ""
    if (editor != null) {
        element?.let {
            var parent = element.parent
            parent?.let {
                when (parent) {
                    is JSFunction -> {
                        val genResult = genUseMemoCodeByFunction(parent, hooksName)
                        code = genResult.code
                        parent = genResult.originElement
                    }
                    is JSBlockStatement -> {
                        if (parent.context is JSFunction) {
                            val genResult = genUseMemoCodeByFunction(parent.context, hooksName)
                            code = genResult.code
                            parent = genResult.originElement
                        }
                    }
                    is JSVarStatement -> {
                        code = genUseHooksCode(parent as JSVarStatement, hooksName)
                    }
                    is JSParameter, is JSParameterList -> {
                        parent = PsiTreeUtil.findFirstContext(parent, true){
                            it is JSFunction
                        }
                        val grandFather = PsiTreeUtil.findFirstContext(parent, true) {
                            it is JSVarStatement
                        }
                        if (grandFather != null) {
                            parent = grandFather
                        }
                        code = genUseHooksCode(parent, hooksName)
                    }
                    is JSVariable, is JSDestructuringObject-> {
                        code = if (parent.context is JSDestructuringProperty || parent.context is JSDestructuringArray || parent is JSDestructuringObject) {
                            parent = PsiTreeUtil.getContextOfType(parent, JSVarStatement::class.java, true )
                            genUseHooksCode(parent as JSVarStatement, hooksName)
                        } else {
                            genUseHooksCode(parent.context as JSVarStatement, hooksName)
                        }

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
}

private fun genUseMemoCodeByFunction(element: PsiElement?, hooksName: String): GenResult {
    var parent = element

    val grandFather = PsiTreeUtil.findFirstContext(parent, true) {
        it is JSVarStatement
    }
    if (grandFather != null) {
        parent = grandFather
    }
    return GenResult(genUseHooksCode(parent, hooksName), parent)
}

