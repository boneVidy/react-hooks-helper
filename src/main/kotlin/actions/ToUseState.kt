package actions
import com.intellij.ide.projectView.ProjectView
import com.intellij.lang.javascript.psi.JSDestructuringArray
import com.intellij.lang.javascript.psi.JSDestructuringProperty
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiFile
import gen.genUseStateByVarStatement
import gen.genUseStateByVarStatementWithDestruct
import io.reWriteCode
import utils.getOffsetRange
import utils.getVarStatementByDestructuringProperty


class ToUseState: AnAction(){
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val document =editor?.document
        val project = event.getData(CommonDataKeys.PROJECT)
        val psiFile: PsiFile? = event.getData(LangDataKeys.PSI_FILE)
        if (editor != null && psiFile != null) {
            val caretModel = editor.caretModel
            val element = psiFile.findElementAt(caretModel.offset)
            element?.let {
                var parent = element.parent
                var code = ""
                parent?.let {
                    when (parent) {
                        is JSVarStatement -> {
                            code = genUseStateByVarStatement(parent as JSVarStatement)
                        }
                        is JSVariable -> {
                            code = if (parent.context is JSDestructuringProperty || parent.context is JSDestructuringArray) {
                                parent = getVarStatementByDestructuringProperty(parent.context!!)
                                genUseStateByVarStatementWithDestruct(parent as JSVarStatement)
                            } else {
                                genUseStateByVarStatement(parent.context as JSVarStatement)
                            }

                        }
                    }
                }
                val pair = getOffsetRange(parent)
                val startOffset: Int = pair.first
                val endOffset: Int = pair.second
                reWriteCode(project, document, code, startOffset, endOffset)
                ProjectView.getInstance(project).refresh()
                event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
            }
        }
    }
}



