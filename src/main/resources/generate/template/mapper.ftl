package ${javaMapper.packageName};

import ${tableClass.fullClassName};
<#if tableClass.pkFields??>
    <#list tableClass.pkFields as field><#assign pkName>${field.shortTypeName}</#assign></#list>
</#if>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Entity ${tableClass.fullClassName}
 */
public interface ${javaMapper.fileName} extends BaseMapper<${tableClass.shortClassName}> {

}




