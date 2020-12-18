package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import gen.genCodeBuAnActionEvent


class ToUseCallback : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        genCodeBuAnActionEvent(event, "useCallback")
    }

}

