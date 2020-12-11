package icons.io

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

fun reWriteCode(
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