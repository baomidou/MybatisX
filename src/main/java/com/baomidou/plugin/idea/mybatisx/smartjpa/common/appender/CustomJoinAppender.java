package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.JoinAppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CustomJoinAppender implements SyntaxAppender {

    public static final String SPACE = " ";
    private final String tipText;
    private String sqlText;
    private AreaSequence areaSequence;

    public CustomJoinAppender(String tipText, String sqlText, AreaSequence areaSequence) {
        this.tipText = tipText;
        this.sqlText = sqlText;
        this.areaSequence = areaSequence;
    }

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    @Override
    public String getText() {
        return this.tipText;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.JOIN;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Arrays.asList(new JoinAppendTypeCommand(this));
    }

    @Override
    public Optional<SyntaxAppender> pollLast(LinkedList<SyntaxAppender> splitList) {
        final Optional<SyntaxAppender> syntaxAppender = Optional.of(splitList.pollLast());
        if (syntaxAppender.isPresent() && splitList.size() > 0) {
            final SyntaxAppender last = splitList.getLast();
            return last.pollLast(splitList);
        }
        return syntaxAppender;
    }

    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        if (result.isEmpty()) {
            result.add(this);
            return true;
        }
        return false;
    }

    @Override
    public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<TreeWrapper<SyntaxAppender>> collector) {
        return "\n" + sqlText + SPACE;
    }

    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, TreeWrapper<SyntaxAppender> treeWrapper) {
        treeWrapper.addWrapper(new TreeWrapper<>(this));
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomJoinAppender.class);


    @Override
    public String toString() {
        return "CustomJoinAppender{" +
            "tipText='" + tipText + '\'' +
            ", sqlText='" + sqlText + '\'' +
            ", areaSequence=" + areaSequence +
            '}';
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
//        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean sequenceCheck = getAreaSequence().getSequence() == secondAppender.getAreaSequence().getSequence();
        return sequenceCheck;
    }
}
