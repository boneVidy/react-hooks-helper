package actions
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiFile
import icons.gen.genUseStateCode
import icons.io.reWriteCode
import icons.utils.getOffsetRange


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
                val parent = element.parent
                val pair = getOffsetRange(parent)
                val startOffset: Int = pair.first
                val endOffset: Int = pair.second
                val code = genUseStateCode(parent)
                reWriteCode(project, document, code, startOffset, endOffset)
                ProjectView.getInstance(project).refresh()
                event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
            }
        }
    }
}



