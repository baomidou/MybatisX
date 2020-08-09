package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AreaPrefixAppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.TreeWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.*;

public class CustomAreaAppender implements SyntaxAppender {
    @Override
    public String toString() {
        return "CustomAreaAppender{" +
            "area='" + area + '\'' +
            ", areaType='" + areaType + '\'' +
            ", areaSequence=" + areaSequence +
            ", syntaxAppenderFactory=" + syntaxAppenderFactory +
            ", childAreaSequence=" + childAreaSequence +
            '}';
    }

    private final String area;

    private final String areaType;

    private AreaSequence areaSequence;

    private SyntaxAppenderFactory syntaxAppenderFactory;

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    protected CustomAreaAppender(final String area, String areaType, AreaSequence areaSequence, AreaSequence childAreaSequence, SyntaxAppenderFactory syntaxAppenderFactory) {
        this.area = area;
        this.areaType = areaType;
        this.areaSequence = areaSequence;
        this.childAreaSequence = childAreaSequence;
        this.syntaxAppenderFactory = syntaxAppenderFactory;
    }


    protected CustomAreaAppender(String area, String areaType, AreaSequence areaSequence, SyntaxAppenderFactory syntaxAppenderFactory) {
        this.area = area;
        this.areaType = areaType;
        this.areaSequence = areaSequence;
        this.syntaxAppenderFactory = syntaxAppenderFactory;
    }

    public static CustomAreaAppender createCustomAreaAppender(String area,
                                                              String areaType,
                                                              AreaSequence areaSequence,
                                                              SyntaxAppenderFactory syntaxAppenderFactory) {
        return createCustomAreaAppender(area, areaType, areaSequence, AreaSequence.UN_KNOWN, syntaxAppenderFactory);
    }

    public static CustomAreaAppender createCustomAreaAppender(final String area, String areaType) {
        return createCustomAreaAppender(area, areaType, AreaSequence.AREA, AreaSequence.UN_KNOWN, null);
    }

    public static CustomAreaAppender createCustomAreaAppender(final String area, String areaType, AreaSequence sequence,
                                                              AreaSequence childAreaSequence,
                                                              SyntaxAppenderFactory syntaxAppenderFactory) {
        return new CustomAreaAppender(area, areaType, sequence, childAreaSequence, syntaxAppenderFactory);
    }

    @Override
    public String getText() {
        return this.area;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.AREA;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Arrays.asList(new AreaPrefixAppendTypeCommand(this.area, areaType, getAreaSequence(), getChildAreaSequence(), syntaxAppenderFactory));
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
    public boolean checkDuplicate(Set<String> syntaxAppenders) {
        return syntaxAppenders.add(areaType);
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
        return syntaxAppenderFactory.getTemplateText(tableName, entityClass, parameters, collector);
    }


    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, TreeWrapper<SyntaxAppender> treeWrapper) {
        SyntaxAppender currentAppender = jpaStringList.peek();

        while (currentAppender != null) {
            if (jpaStringList.peek() == null || jpaStringList.peek().getType() == AppendTypeEnum.AREA) {
                break;
            }
            currentAppender = jpaStringList.poll();
            if (currentAppender != null) {
                currentAppender.toTree(jpaStringList, treeWrapper);
            }
        }
    }


    private AreaSequence childAreaSequence;

    public AreaSequence getChildAreaSequence() {
        return childAreaSequence;
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = areaSequence.getSequence() >= getChildAreaSequence().getSequence();
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean sequenceCheck = (getAreaSequence() == areaSequence);
        return hasAreaCheck || (typeCheck && sequenceCheck);
    }
}
