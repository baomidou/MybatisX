package com.baomidou.plugin.idea.mybatisx.inspection;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Sql;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.sql.inspections.suppression.SqlInspectionSuppressor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MapperSqlInspectionsFilter extends SqlInspectionSuppressor implements InspectionSuppressor {

    private static final Set<String> COLUMN_NOT_CHECK = new HashSet<String>() {
        {
            add("BASE_COLUMN_LIST");
            add("COLUMN_LIST");
            add("COLUMN_SQL");
        }
    };
    public static final SuppressQuickFix[] EMPTY_FIX = new SuppressQuickFix[0];

    /**
     * 对于特殊的sql标签, 验证错误时, 不在提示修复
     * <sql id="Base_Column_List">
     * id, cname, ckey, status, create_time, update_time
     * </sql>
     *
     * @param element
     * @param toolId
     * @return
     */
    @NotNull
    @Override
    public SuppressQuickFix[] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        final SuppressQuickFix[] suppressActions = super.getSuppressActions(element, toolId);
        if (element != null && suppressActions.length > 0) {
            final Project project = element.getProject();
            final InjectedLanguageManager instance = InjectedLanguageManager.getInstance(project);
            final PsiFile topLevelFile = instance.getTopLevelFile(element);
            final int offset = instance.injectedToHost(element, 0);
            final PsiElement elementFromXml = topLevelFile.findElementAt(offset);
            final Optional<IdDomElement> parentIdDomElement = MapperUtils.findParentIdDomElement(elementFromXml);
            if (parentIdDomElement.isPresent()) {
                final IdDomElement idDomElement = parentIdDomElement.get();
                if (idDomElement instanceof Sql) {
                    String stringValue = idDomElement.getId().getStringValue();
                    if (stringValue == null) {
                        stringValue = "";
                    }
                    stringValue = stringValue.toUpperCase();
                    for (String ignoredId : COLUMN_NOT_CHECK) {
                        if (stringValue.contains(ignoredId)) {
                            return EMPTY_FIX;
                        }
                    }
                }
            }
        }
        return suppressActions;
    }
}
