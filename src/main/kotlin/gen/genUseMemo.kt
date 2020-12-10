package icons.gen

import com.intellij.lang.javascript.psi.impl.JSVarStatementImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType

//import com.intellij.lang.javascript.psi.JSVarStatement
//
public fun genUseMemoByVarStatement (varStatement: JSVarStatementImpl):String {

    val ret = ""
    val varTypeText = varStatement.children[0].text;
//    varStatement.children[1].children.
    val psiReferenceMap = mutableMapOf<String, MutableSet<PsiReference>>()
    val depMap = getDependencies(varStatement, psiReferenceMap)
    val varKeyword = varStatement.varKeyword
    return """$varKeyword """

}

private fun getDependencies (psiElement: PsiElement, psiReferenceMap: MutableMap<String, MutableSet<PsiReference>>): MutableMap<String, MutableSet<PsiReference>> {
    if (psiElement.elementType.toString() == "JS:REFERENCE_EXPRESSION") {
        if(psiReferenceMap[psiElement.text].isNullOrEmpty()) {
            val set = mutableSetOf<PsiReference>()
            set.add(psiElement.references[0])
            psiReferenceMap[psiElement.text] = set
        } else {
            psiReferenceMap[psiElement.text]?.add(psiElement.references[0])
        }
        return psiReferenceMap
    }
    if (psiElement.children.isNotEmpty()) {
        psiElement.children.forEach { getDependencies(it, psiReferenceMap)}
    }

    return psiReferenceMap;
}

public fun genUseMemoByFunction ():String {
    val ret = ""

    return ret
}
