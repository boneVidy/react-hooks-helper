package actions
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import icons.consts.PsiElementTypeConst
import icons.gen.genUseMemoCode
import icons.io.reWriteCode


class ToUseMemo: AnAction(){
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val document =editor?.document
        val project = event.getData(CommonDataKeys.PROJECT)
        val psiFile: PsiFile? = event.getData(LangDataKeys.PSI_FILE)
        if (editor != null && psiFile != null) {
            val caretModel = editor.caretModel
            val element = psiFile.findElementAt(caretModel.offset)
            element?.let {
                println(element.parent)
                val parent = element.parent
                val range = parent.textRange
                val psiType: IElementType? = parent.elementType
                var startOffset:Int = 0
                var endOffset:Int = 0
                parent?.let {
                    when {
                        psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT || psiType.toString() == PsiElementTypeConst.TYPESCRIPT_FUNCTION -> {
                            startOffset = range.startOffset
                            endOffset = range.endOffset
                        }
                        else -> {
                            startOffset = parent.context?.textRange?.startOffset ?: 0
                            endOffset = parent.context?.textRange?.endOffset ?: 0
                        }
                    }
                }
                val code = genUseMemoCode(parent)
                reWriteCode(project, document, code, startOffset, endOffset)
                ProjectView.getInstance(project).refresh()
                event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
            }


        }

    }



}

