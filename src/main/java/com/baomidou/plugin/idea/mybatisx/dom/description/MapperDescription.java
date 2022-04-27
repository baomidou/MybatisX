package com.baomidou.plugin.idea.mybatisx.dom.description;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * mapper.xml 文件属性提示
 * </p>
 *
 * @author yanglin jobob
 * @since 2018 -07-30
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public static final String [] HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD =
        new String[]{"http://mybatis.org/dtd/mybatis-3-mapper.dtd",
            "http://www.mybatis.org/dtd/mybatis-3-mapper.dtd"
    };

    /**
     * Instantiates a new Mapper description.
     */
    public MapperDescription() {
        super(Mapper.class, "mapper");
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("MybatisXml",
            HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD);
    }
}
