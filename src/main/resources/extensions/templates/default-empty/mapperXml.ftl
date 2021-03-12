<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperInterface.packageName}.${baseInfo.fileName}">

    <resultMap id="BaseResultMap" type="${tableClass.fullClassName}">
        <#list tableClass.pkFields as field>
            <id property="${field.fieldName}" column="${field.columnName}" jdbcType="${field.jdbcType}"/>
        </#list>
        <#list tableClass.baseFields as field>
            <result property="${field.fieldName}" column="${field.columnName}" jdbcType="${field.jdbcType}"/>
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list tableClass.allFields as field>${field.columnName}<#sep>,<#if field_index%3==2>${"\n        "}</#if></#list>
    </sql>
</mapper>
