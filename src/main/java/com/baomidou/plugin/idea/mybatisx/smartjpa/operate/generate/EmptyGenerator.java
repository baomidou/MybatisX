package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmptyGenerator implements Generator {
    private Logger logger = LoggerFactory.getLogger(EmptyGenerator.class);
    @Override
    public void generateSelect(String id, String value, Boolean resultType, String resultMap, String resultSet, List<TxField> resultFields, PsiClass entityClass) {
        logger.warn("generateSelect fail");
    }

    @Override
    public void generateDelete(String id, String value) {
        logger.warn("generateDelete fail");
    }

    @Override
    public void generateInsert(String id, String value) {
        logger.warn("generateInsert fail");
    }

    @Override
    public void generateUpdate(String id, String value) {
        logger.warn("generateUpdate fail");
    }
}
