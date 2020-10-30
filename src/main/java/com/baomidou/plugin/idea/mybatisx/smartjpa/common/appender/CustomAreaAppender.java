package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.command.AreaPrefixAppendTypeCommand;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The type Custom area appender.
 */
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

    /**
     * Gets syntax appender factory.
     *
     * @return the syntax appender factory
     */
    public SyntaxAppenderFactory getSyntaxAppenderFactory() {
        return syntaxAppenderFactory;
    }

    private final String area;

    private final String areaType;

    private AreaSequence areaSequence;

    private SyntaxAppenderFactory syntaxAppenderFactory;

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    /**
     * Instantiates a new Custom area appender.
     *
     * @param area                  the area
     * @param areaType              the area type
     * @param areaSequence          the area sequence
     * @param childAreaSequence     the child area sequence
     * @param syntaxAppenderFactory the syntax appender factory
     */
    protected CustomAreaAppender(final String area, String areaType, AreaSequence areaSequence, AreaSequence childAreaSequence, SyntaxAppenderFactory syntaxAppenderFactory) {
        this.area = area;
        this.areaType = areaType;
        this.areaSequence = areaSequence;
        this.childAreaSequence = childAreaSequence;
        this.syntaxAppenderFactory = syntaxAppenderFactory;
    }


    /**
     * Instantiates a new Custom area appender.
     *
     * @param area                  the area
     * @param areaType              the area type
     * @param areaSequence          the area sequence
     * @param syntaxAppenderFactory the syntax appender factory
     */
    protected CustomAreaAppender(String area, String areaType, AreaSequence areaSequence, SyntaxAppenderFactory syntaxAppenderFactory) {
        this.area = area;
        this.areaType = areaType;
        this.areaSequence = areaSequence;
        this.syntaxAppenderFactory = syntaxAppenderFactory;
    }

    /**
     * Create custom area appender custom area appender.
     *
     * @param area                  the area
     * @param areaType              the area type
     * @param areaSequence          the area sequence
     * @param syntaxAppenderFactory the syntax appender factory
     * @return the custom area appender
     */
    public static CustomAreaAppender createCustomAreaAppender(String area,
                                                              String areaType,
                                                              AreaSequence areaSequence,
                                                              SyntaxAppenderFactory syntaxAppenderFactory) {
        return createCustomAreaAppender(area, areaType, areaSequence, AreaSequence.UN_KNOWN, syntaxAppenderFactory);
    }

    /**
     * Create custom area appender custom area appender.
     *
     * @param area     the area
     * @param areaType the area type
     * @return the custom area appender
     */
    public static CustomAreaAppender createCustomAreaAppender(final String area, String areaType) {
        return createCustomAreaAppender(area, areaType, AreaSequence.AREA, AreaSequence.UN_KNOWN, null);
    }

    /**
     * Create custom area appender custom area appender.
     *
     * @param area                  the area
     * @param areaType              the area type
     * @param sequence              the sequence
     * @param childAreaSequence     the child area sequence
     * @param syntaxAppenderFactory the syntax appender factory
     * @return the custom area appender
     */
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
    public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<PsiParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
        return syntaxAppenderFactory.getTemplateText(tableName, entityClass, parameters, collector,conditionFieldWrapper);
    }


    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        SyntaxAppender currentAppender = jpaStringList.peek();

        while (currentAppender != null) {
            if (jpaStringList.peek() == null || jpaStringList.peek().getType() == AppendTypeEnum.AREA) {
                break;
            }
            currentAppender = jpaStringList.poll();
            if (currentAppender != null) {
                currentAppender.toTree(jpaStringList, syntaxAppenderWrapper);
            }
        }
    }


    private AreaSequence childAreaSequence;

    /**
     * Gets child area sequence.
     *
     * @return the child area sequence
     */
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
