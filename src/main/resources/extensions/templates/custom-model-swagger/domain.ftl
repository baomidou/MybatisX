package ${domain.packageName};

<#list tableClass.allFields as field>
    <#if !field.fullTypeName?starts_with("java.lang") && !(field.columnIsArray)>
import ${field.fullTypeName};
    </#if>
</#list>
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
* ${tableClass.remark!}
* @TableName ${tableClass.tableName}
*/
public class ${tableClass.shortClassName} {

<#list tableClass.allFields as field>
    /**
    * ${field.remark!}
    */
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
