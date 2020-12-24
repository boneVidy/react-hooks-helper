package actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import gen.genUseMemoOrUseCallbackCode
import utils.isHooksStatement


class ToUseCallbackIntentionAction: PsiElementBaseIntentionAction(), IntentionAction {
    override fun getFamilyName(): String {
        return "convert to useCallback"
    }

    override fun getText (): String {
        return "convert to useCallback"
    }
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {

        val parent = PsiTreeUtil.findFirstContext(element, true) {
            it is JSVarStatement || it is JSFunction
        }
        if (parent != null && isHooksStatement(parent)) {
            return false
        }
        val childFunction = PsiTreeUtil.findChildOfType(parent, JSFunction::class.java, true)
        if (parent is JSVarStatement && childFunction == null) {
            return false
        }
        return parent != null
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        genUseMemoOrUseCallbackCode(editor, project, element, "useCallback")
    }

}