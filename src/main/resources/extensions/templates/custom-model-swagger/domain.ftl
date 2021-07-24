package ${domain.packageName};

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
<#list tableClass.importList as fieldType>${"\n"}import ${fieldType};</#list>
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
* ${tableClass.remark!}
* @TableName ${tableClass.tableName}
*/
public class ${tableClass.shortClassName} implements Serializable {

<#list tableClass.allFields as field>
    /**
    * ${field.remark!}
    */<#if !field.nullable || field.jdbcType=="VARCHAR">${"\n    "}</#if><#if !field.nullable><#if field.jdbcType=="VARCHAR">@NotBlank(message="[${field.remark!}]不能为空")<#else>@NotNull(message="[${field.remark!}]不能为空")</#if></#if><#if field.jdbcType=="VARCHAR"><#if !field.nullable>${"\n    "}</#if>@Size(max= ${field.columnLength?c},message="编码长度不能超过${field.columnLength?c}")</#if>
    @ApiModelProperty("${field.remark!}")<#if field.jdbcType=="VARCHAR">${"\n    "}@Length(max= ${field.columnLength},message="编码长度不能超过${field.columnLength}")</#if>
    private ${field.shortTypeName} ${field.fieldName};
</#list>

<#list tableClass.allFields as field>
    /**
    * ${field.remark!}
    */
    private void set${field.fieldName?cap_first}(${field.shortTypeName} ${field.fieldName}){
    this.${field.fieldName} = ${field.fieldName};
    }

</#list>

<#list tableClass.allFields as field>
    /**
    * ${field.remark!}
    */
    private ${field.shortTypeName} get${field.fieldName?cap_first}(){
    return this.${field.fieldName};
    }

</#list>
}
