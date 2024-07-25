<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperInterface.packageName}.${baseInfo.fileName}">

    <insert id="insert"<#if (tableClass.pkFields?size==1)> keyColumn="${tableClass.pkFields[0].columnName}" keyProperty="${tableClass.pkFields[0].fieldName}" parameterType="${tableClass.fullClassName}" useGeneratedKeys="true"</#if>>
        insert into ${tableClass.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list tableClass.allFields as field>
                <if test="${field.fieldName} != null">${field.columnName},</if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list tableClass.allFields as field>
                <if test="${field.fieldName} != null">${'#'}{${field.fieldName}},</if>
            </#list>
        </trim>
    </insert>
    <update id="update" parameterType="${tableClass.fullClassName}">
        update ${tableClass.tableName}
        <set>
            <#list tableClass.baseBlobFields as field>
                <if test="${field.fieldName} != null">
                    ${field.columnName} = ${'#'}{${field.fieldName}},
                </if>
            </#list>
        </set>
        where <#list tableClass.pkFields as field>${field.columnName} = ${'#'}{${field.fieldName}} <#if field_has_next>AND</#if></#list>
    </update>
</mapper>
