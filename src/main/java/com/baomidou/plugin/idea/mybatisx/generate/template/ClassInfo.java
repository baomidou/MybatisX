package com.baomidou.plugin.idea.mybatisx.generate.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.FieldInfo;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import lombok.Getter;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实体类的信息
 */
@Getter
public class ClassInfo {
    /**
     * 类的全称(包括包名)
     */
    private String fullClassName;
    /**
     * 类的简称
     */
    private String shortClassName;
    /**
     * 小写简称
     */
    private String lowerFirstShortClassName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表的注释
     */
    private String remark;
    /**
     * 主键字段列表
     */
    private List<FieldInfo> pkFields;
    /**
     * 全部字段
     */
    private List<FieldInfo> allFields;
    /**
     * 除了主键的所有字段
     */
    private List<FieldInfo> baseFields;
    /**
     * 所有的blob字段
     */
    private List<FieldInfo> baseBlobFields;
    /**
     * 需要导入的实体类的所有字段类型
     */
    private List<String> importList;

    public static ClassInfo build(IntrospectedTable introspectedTable) {
        ClassInfo classInfo = new ClassInfo();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        classInfo.fullClassName = introspectedTable.getBaseRecordType();
        classInfo.shortClassName = type.getShortName();
        classInfo.lowerFirstShortClassName = StringUtils.firstToLowerCase(classInfo.shortClassName);
        classInfo.tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        classInfo.remark = introspectedTable.getRemarks() == null ? "" : introspectedTable.getRemarks();

        classInfo.pkFields = introspectedTable.getPrimaryKeyColumns()
            .stream()
            .map(FieldInfo::build)
            .collect(Collectors.toList());

        classInfo.allFields = Stream.of(introspectedTable.getPrimaryKeyColumns(),
                introspectedTable.getBaseColumns(),
                introspectedTable.getBLOBColumns())
            .flatMap(Collection::stream)
            .map(FieldInfo::build)
            .collect(Collectors.toList());

        classInfo.baseFields = introspectedTable.getBaseColumns().stream()
            .map(FieldInfo::build)
            .collect(Collectors.toList());

        classInfo.baseBlobFields = Stream.of(introspectedTable.getBaseColumns(),
                introspectedTable.getBLOBColumns())
            .flatMap(Collection::stream)
            .map(FieldInfo::build)
            .collect(Collectors.toList());
        // 拿到所有需要import的类型, 不是java.lang包开头的,并且不是数组类型 去重的所有类型
        classInfo.importList = classInfo.allFields.stream()
            .filter(fieldInfo -> !fieldInfo.isColumnIsArray())
            .map(FieldInfo::getFullTypeName)
            .filter(typeName -> !typeName.startsWith("java.lang"))
            .distinct()
            .collect(Collectors.toList());
        return classInfo;
    }

}
