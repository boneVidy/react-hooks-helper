package actions

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil


//import com.intellij.lang.typescript

class ToHooks: AnAction(){
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val document =editor?.document
        val project = event.getData(CommonDataKeys.PROJECT)
        val psiFile: PsiFile? = event.getData(LangDataKeys.PSI_FILE)
//        JSX
//        psiFile?.let {
//            psiFile.ac
//
//
//        }
        val code = "test"
//        val statement:Js
        editor?.let {
            val selectModel = editor.selectionModel
            val caretModel = editor.caretModel
            val offsetStart = if (selectModel.hasSelection()) {
                selectModel.selectionStart
            } else {
                0
            }
            val offsetEnd = if (selectModel.hasSelection()) {
                selectModel.selectionEnd
            } else {
                caretModel.offset
            }
            convertCodeToHooks(project, document, code, offsetStart, offsetEnd)
            ProjectView.getInstance(project).refresh()
            event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
        }

    }
//    private fun find JSVarStateMent ():JS {
//
//    }
//    private fun findInjectParameterList(file: PsiFile): JSParameterList? {
//        // Recursively find all the call expressions in the file.
//        val callExpressions: Collection<JSCallExpression> = PsiTreeUtil.findChildrenOfType(
//            file,
//            JSCallExpression::class.java
//        )
//        for (jsCallExpression in callExpressions) {
//            // Find the inject() element.
//            if (jsCallExpression.getText().startsWith("inject")) {
//                // Get the parameter list. Should be a child of the call expression.
//                return PsiTreeUtil.findChildOfType(jsCallExpression, JSParameterList::class.java)
//            }
//        }
//        return null
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