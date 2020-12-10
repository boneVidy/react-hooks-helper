package actions
import com.intellij.lang.javascript.psi.impl.JSVarStatementImpl
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import icons.gen.genUseMemoByVarStatement


class ToHooks: AnAction(){
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val document =editor?.document
        val project = event.getData(CommonDataKeys.PROJECT)
        val psiFile: PsiFile? = event.getData(LangDataKeys.PSI_FILE)
        if (editor != null && psiFile != null) {
            val selectModel = editor.selectionModel
            val caretModel = editor.caretModel
            val element = psiFile.findElementAt(caretModel.offset)
            element?.let {
                println(element.parent)
                val parent = element.parent
                val parentType = parent.elementType
                var code = ""
                parent?.let {
                    when {
                        parentType.toString() == "JS:VAR_STATEMENT" -> {
                            code = genUseMemoByVarStatement(parent as JSVarStatementImpl)
                        }

                    }
                    convertCodeToHooks(project, document, code, caretModel.offset, caretModel.offset + parent.textLength)
                    ProjectView.getInstance(project).refresh()
                    event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
                }

            }


        }

    }
//    private fun genCodeFromPsiElement (varStatement: JSVarStatement) {
//
//    }
    private fun convertCodeToHooks(
        project: Project?,
        document: Document?,
        code: String,
        offsetStart: Int,
        offsetEnd: Int
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            document?.apply {

                replaceString(offsetStart, offsetEnd, code)

            }
        }
    }


}