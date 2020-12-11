package icons.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import icons.consts.PsiElementTypeConst

fun getOffsetRange(element: PsiElement): Pair<Int, Int> {
    val range = element.textRange
    val psiType: IElementType? = element.elementType
    val startOffset: Int
    val endOffset: Int
    when {
        psiType.toString() == PsiElementTypeConst.JS_VAR_STATEMENT || psiType.toString() == PsiElementTypeConst.TYPESCRIPT_FUNCTION -> {
            startOffset = range.startOffset
            endOffset = range.endOffset
        }
        else -> {
            startOffset = element.context?.textRange?.startOffset ?: 0
            endOffset = element.context?.textRange?.endOffset ?: 0
        }
    }
    return Pair( startOffset, endOffset)
}