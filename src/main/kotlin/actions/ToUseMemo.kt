package actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import gen.genCodeBuAnActionEvent


class ToUseMemo : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        genCodeBuAnActionEvent(event, "useMemo")
    }
}

