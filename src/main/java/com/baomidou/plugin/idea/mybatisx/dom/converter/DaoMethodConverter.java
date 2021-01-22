package com.baomidou.plugin.idea.mybatisx.dom.converter;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Dao method converter.
 *
 * @author yanglin
 */
public class DaoMethodConverter extends ConverterAdaptor<Object> {

    /**
     * id 的转换允许有空值 ， （这是一个不合理的结构）
     * 例如 selectKey 没有id
     * select,insert,update,delete 有 id
     *
     * @param id
     * @param context
     * @return
     */
    @Nullable
    @Override
    public Object fromString(@Nullable @NonNls String id, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        Optional<PsiMethod> method = JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id);
        if (method.isPresent()) {
            return method.get();
        }
        return context.getInvocationElement();
    }

}
