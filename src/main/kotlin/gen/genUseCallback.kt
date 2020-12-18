package gen

import com.intellij.psi.PsiElement
import gen.genUseHooksCode

fun genUseCallBackCode(
    psiEle: PsiElement?
): String {
    return genUseHooksCode(psiEle, "useCallback")
}