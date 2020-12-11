package icons.gen

import com.intellij.psi.PsiElement

fun genUseCallBackCode(
    psiEle: PsiElement?
): String {
    return genUseHooksCode(psiEle, "useCallback")
}