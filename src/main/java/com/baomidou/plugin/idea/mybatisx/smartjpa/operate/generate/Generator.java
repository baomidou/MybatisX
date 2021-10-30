package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * The interface Generator.
 */
public interface Generator {
    /**
     * Generate select.
     *
     * @param id           the id
     * @param value        the value
     * @param resultType
     * @param resultMap    the result map
     * @param resultSet    the result set
     * @param resultFields
     * @param entityClass
     */
    void generateSelect(String id,
                        String value,
                        Boolean resultType,
                        String resultMap,
                        String resultSet,
                        List<TxField> resultFields,
                        PsiClass entityClass);

    /**
     * Generate delete.
     *
     * @param id    the id
     * @param value the value
     */
    void generateDelete(String id, String value);

    /**
     * Generate insert.
     *
     * @param id    the id
     * @param value the value
     */
    void generateInsert(String id, String value);

    /**
     * Generate update.
     *
     * @param id    the id
     * @param value the value
     */
    void generateUpdate(String id, String value);

    /**
     * 检查是否能生成代码
     * @param mapperClass mapper接口类
     * @return 能还是不能？
     */
    boolean checkCanGenerate(PsiClass mapperClass);

}
