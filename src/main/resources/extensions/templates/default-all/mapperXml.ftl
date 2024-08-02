<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperInterface.packageName}.${baseInfo.fileName}">

    <resultMap id="BaseResultMap" type="${tableClass.fullClassName}">
        <#list tableClass.pkFields as field>
            <id property="${field.fieldName}" column="${field.columnName}" <#if baseInfo.needJdbcType>jdbcType="${field.jdbcType}"</#if>/>
        </#list>
        <#list tableClass.baseFields as field>
            <result property="${field.fieldName}" column="${field.columnName}" <#if baseInfo.needJdbcType>jdbcType="${field.jdbcType}"</#if>/>
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list tableClass.allFields as field>${field.columnName}<#sep>,<#if field_index%5==0>${"\n        "}</#if></#list>
    </sql>

    <select id="selectByPrimaryKey" parameterType="java.lang.Long" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${tableClass.tableName}
        where <#list tableClass.pkFields as field> ${field.columnName} = ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>} <#if field_has_next>AND</#if></#list>
    </select>

    <delete id="deleteByPrimaryKey" parameterType="java.lang.Long">
        delete from ${tableClass.tableName}
        where <#list tableClass.pkFields as field> ${field.columnName} = ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>} <#if field_has_next>AND</#if></#list>
    </delete>
    <insert id="insert"<#if (tableClass.pkFields?size==1)> keyColumn="${tableClass.pkFields[0].columnName}" keyProperty="${tableClass.pkFields[0].fieldName}" parameterType="${tableClass.fullClassName}" useGeneratedKeys="true"</#if>>
        insert into ${tableClass.tableName}
        ( <#list tableClass.allFields as field>${field.columnName}<#if field_index%5==0>${"\n        "}</#if><#sep>,</#list>)
        values (<#list tableClass.allFields as field>${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>}<#if field_index%5==0>${"\n        "}</#if><#sep>,</#list>)
    </insert>
    <insert id="insertSelective"<#if (tableClass.pkFields?size==1)> keyColumn="${tableClass.pkFields[0].columnName}" keyProperty="${tableClass.pkFields[0].fieldName}" parameterType="${tableClass.fullClassName}" useGeneratedKeys="true"</#if>>
        insert into ${tableClass.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list tableClass.allFields as field>
                <if test="${field.fieldName} != null">${field.columnName},</if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list tableClass.allFields as field>
                <if test="${field.fieldName} != null">${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>},</if>
            </#list>
        </trim>
    </insert>
    <update id="updateByPrimaryKeySelective" parameterType="${tableClass.fullClassName}">
        update ${tableClass.tableName}
        <set>
            <#list tableClass.baseBlobFields as field>
                <if test="${field.fieldName} != null">
                    ${field.columnName} = ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>},
                </if>
            </#list>
        </set>
        where  <#list tableClass.pkFields as field> ${field.columnName} = ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>} <#if field_has_next>AND</#if></#list>
    </update>
    <update id="updateByPrimaryKey" parameterType="${tableClass.fullClassName}">
        update ${tableClass.tableName}
        set <#list tableClass.baseBlobFields as field>
            ${field.columnName} =  ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>}<#sep>,</#list>
        where  <#list tableClass.pkFields as field> ${field.columnName} = ${'#'}{${field.fieldName}<#if baseInfo.needJdbcType>,jdbcType=${field.jdbcType}</#if>} <#if field_has_next>AND</#if></#list>
    </update>
</mapper>
