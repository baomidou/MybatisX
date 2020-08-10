package com.baomidou.plugin.idea.mybatisx.smartjpa.common.factory;


import com.baomidou.plugin.idea.mybatisx.smartjpa.common.BaseAppenderFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.AreaSequence;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CompositeAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomAreaAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomFieldAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomJoinAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.CustomSuffixAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer.BetweenParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer.InParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.changer.NotInParameterChanger;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender.operator.suffix.ParamIgnoreCaseSuffixOperator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.StringUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.SyntaxAppenderWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 条件追加区
 */
public class ConditionAppenderFactory extends BaseAppenderFactory {
    List<SyntaxAppender> syntaxAppenderArrayList;

    public ConditionAppenderFactory(final String resultAreaName, final List<TxField> mappingField) {
        syntaxAppenderArrayList = initAppender(mappingField, resultAreaName);
    }

    public List<SyntaxAppender> initAppender(List<TxField> mappingField, String resultAreaName) {
        final List<SyntaxAppender> syntaxAppenderArrayList = new ArrayList<>();

        for (final TxField field : mappingField) {

            // 区域条件 : select + By + field
            final CompositeAppender areaByAppender = new CompositeAppender(
                CustomAreaAppender.createCustomAreaAppender(resultAreaName, "Result", AreaSequence.AREA, AreaSequence.CONDITION, this),
                CustomAreaAppender.createCustomAreaAppender(this.getTipText(), getTipText(), AreaSequence.AREA, AreaSequence.CONDITION, this),
                new CustomFieldAppender(field, AreaSequence.CONDITION));
            syntaxAppenderArrayList.add(areaByAppender);
            // 区域 :  By + field
            final CompositeAppender areaFieldAppender = new CompositeAppender(
                CustomAreaAppender.createCustomAreaAppender(this.getTipText(), getTipText(), AreaSequence.AREA, AreaSequence.CONDITION, this),
                new CustomFieldAppender(field, AreaSequence.CONDITION));
            syntaxAppenderArrayList.add(areaFieldAppender);

            // and :  and + field
            final CompositeAppender andAppender = new CompositeAppender(
                new CustomJoinAppender("And", "AND", AreaSequence.CONDITION),
                new CustomFieldAppender(field, AreaSequence.CONDITION));
            syntaxAppenderArrayList.add(andAppender);
            // or : or + field
            final CompositeAppender orAppender = new CompositeAppender(
                new CustomJoinAppender("Or", "OR", AreaSequence.CONDITION),
                new CustomFieldAppender(field, AreaSequence.CONDITION));
            syntaxAppenderArrayList.add(orAppender);
        }
        // 添加字段后缀
        // where x.firstname = ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("Equals", "=", AreaSequence.CONDITION));
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("Is", "=", AreaSequence.CONDITION));
        // where x.startDate between ?1 and ?2
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParameterChanger("Between", new BetweenParameterChanger(), AreaSequence.CONDITION));
        // where x.age < ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("LessThan", "<", AreaSequence.CONDITION));
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("Before", "<", AreaSequence.CONDITION));
        // where x.age <= ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("LessThanEqual", "<=", AreaSequence.CONDITION));
        // where x.age > ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("GreaterThan", ">", AreaSequence.CONDITION));
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("After", ">", AreaSequence.CONDITION));
        // where x.age >= ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("GreaterThanEqual", ">=", AreaSequence.CONDITION));

        // where x.age is null
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("IsNull", "is null", AreaSequence.CONDITION));
        // where x.age not null
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("IsNotNull", "is not null", AreaSequence.CONDITION));
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("NotNull", "is not null", AreaSequence.CONDITION));
        // where x.firstname like ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("Like", "like", AreaSequence.CONDITION));
        // where x.firstname not like ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("NotLike", "not like", AreaSequence.CONDITION));

        //  where x.firstname like %?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamAround("StartWith", "like CONCAT('%',", ")", AreaSequence.CONDITION));
        //   where x.firstname like ?1%
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamAround("EndWith", "like CONCAT(", ",'%')", AreaSequence.CONDITION));
        //   where x.firstname like %?1%
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamAround("Containing", "like CONCAT('%',", ",'%')", AreaSequence.CONDITION));
        // where x.lastname <> ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParamJoin("Not", "<>", AreaSequence.CONDITION));
        // where x.age in ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParameterChanger("In", new InParameterChanger(), AreaSequence.CONDITION));
        // where x.age not in ?1
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByParameterChanger("NotIn", new NotInParameterChanger(), AreaSequence.CONDITION));

        // where x.active = true
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("True", "= true", AreaSequence.CONDITION));
        // where x.active = false
        syntaxAppenderArrayList.add(CustomSuffixAppender.createByFixed("False", "= false", AreaSequence.CONDITION));
        // where UPPER(x.firstame) = UPPER(?1)
        syntaxAppenderArrayList.add(CustomSuffixAppender.createBySuffixOperator("IgnoreCase", new ParamIgnoreCaseSuffixOperator(), AreaSequence.CONDITION));

        return syntaxAppenderArrayList;
    }


    @Override
    public List<SyntaxAppender> getSyntaxAppenderList() {
        return syntaxAppenderArrayList;
    }

    @Override
    public String getTipText() {
        return "By";
    }

    private static final Logger logger = LoggerFactory.getLogger(ConditionAppenderFactory.class);

    @Override
    public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList) {
        SyntaxAppender area = jpaStringList.peek();
        if (area == null) {
            return Collections.emptyList();
        }

        if (area.getType() != AppendTypeEnum.AREA ||
            !getTipText().equals(area.getText())) {
            return Collections.emptyList();
        }
        // 移除区域标识
        jpaStringList.poll();

        Map<String, PsiField> fieldMap =
            Arrays.stream(entityClass.getAllFields()).collect(Collectors.toMap(PsiField::getName, x -> x));
        LinkedList<TxParameter> txParameters = new LinkedList<>();
        SyntaxAppender currentAppender = null;
        // 拉到下一个区域
        while ((currentAppender = jpaStringList.poll()) != null
            && currentAppender.getType() != AppendTypeEnum.AREA) {
            if (currentAppender.getType() == AppendTypeEnum.FIELD) {

                String text = StringUtils.lowerCaseFirstChar(currentAppender.getText());
                PsiField psiField = fieldMap.get(text);
                if (psiField == null) {
                    logger.info("字段映射失败,语义字段: {} ", text);
                    continue;
                }
                // 把字段添加到队尾
                txParameters.add(TxParameter.createByPsiField(psiField));

            } else if (currentAppender.getType() == AppendTypeEnum.SUFFIX) {
                // 拿到后缀前面的字段
                TxParameter last = txParameters.pollLast();
                List<TxParameter> suffixParameters = currentAppender.getParameter(last);
                txParameters.addAll(suffixParameters);
            }
        }

        return txParameters;
    }

    @Override
    public String getTemplateText(String tableName, PsiClass entityClass,
                                  LinkedList<PsiParameter> parameters,
                                  LinkedList<SyntaxAppenderWrapper> collector, MybatisXmlGenerator mybatisXmlGenerator) {
        final String where = "where";
        final String condition = collector
            .stream()
            .map(x -> x.getAppender()
                .getTemplateText(tableName, entityClass, parameters, x.getCollector(), mybatisXmlGenerator))
            .collect(Collectors.joining());
        return where + " " + condition;
    }


    @Override
    public void appendDefault(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppender> current) {
        if (syntaxAppender.getType() == AppendTypeEnum.FIELD) {
            current.addLast(CustomSuffixAppender.createByParamJoin("Equals", "=", AreaSequence.CONDITION));
        }
    }


    @Override
    protected AreaSequence getAreaSequence() {
        return AreaSequence.CONDITION;
    }
}