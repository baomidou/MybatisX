package ${domain.packageName};

<#list tableClass.importList as fieldType>${"\n"}import ${fieldType};</#list>
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ${tableClass.shortClassName} {
<#list tableClass.allFields as field>
    @Schema(description="${field.remark!}")
    private ${field.shortTypeName} ${field.fieldName};
</#list>
}
