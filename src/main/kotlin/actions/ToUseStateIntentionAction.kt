package actions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSVarStatement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import gen.genUseStateCode
import utils.canUseHooks
import utils.isHooksStatement


class ToUseStateIntentionAction: PsiElementBaseIntentionAction(), IntentionAction {
    override fun getFamilyName(): String {
        return "convert to useState"
    }
    override fun getText (): String {
        return "convert to useState"
    }
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val isCanUseHooks =  canUseHooks(element)
        if (!isCanUseHooks) {
            return false
        }
        val parent = PsiTreeUtil.findFirstContext(element, true) {
            it is JSVarStatement
        }
        if (parent != null && isHooksStatement(parent)) {
            return false
        }
        val childFunction = PsiTreeUtil.findChildOfType(parent, JSFunction::class.java, true)
        if (parent is JSVarStatement && childFunction == null) {
            return true
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        genUseStateCode(editor, project, element)
    }

}