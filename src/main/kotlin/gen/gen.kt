package gen

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiFile
import io.reWriteCode
import utils.getOffsetRange

fun genCodeBuAnActionEvent(event: AnActionEvent, hooksName: String) {
    val editor = event.getData(CommonDataKeys.EDITOR)
    val document = editor?.document
    val project = event.getData(CommonDataKeys.PROJECT)
    val psiFile: PsiFile? = event.getData(LangDataKeys.PSI_FILE)
    if (editor != null && psiFile != null) {
        val caretModel = editor.caretModel
        val element = psiFile.findElementAt(caretModel.offset)
        element?.let {
            println(element.parent)
            val parent = element.parent
            val pair = getOffsetRange(parent)
            val startOffset: Int = pair.first
            val endOffset: Int = pair.second
            val code = genUseHooksCode(parent, hooksName)
            reWriteCode(project, document, code, startOffset, endOffset)
            ProjectView.getInstance(project).refresh()
            event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
        }
    }
}