package ${baseInfo.packageName};

import ${tableClass.fullClassName};
<#if baseService??&&baseService!="">
    import ${baseService};
    <#list baseService?split(".") as simpleName>
        <#if !simpleName_has_next>
            <#assign serviceSimpleName>${simpleName}</#assign>
        </#if>
    </#list>
</#if>
import com.baomidou.mybatisplus.service.IService;

/**
* @author ${author!}
* @description 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Service
* @createDate ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public interface ${baseInfo.fileName} extends IService<${tableClass.shortClassName}> {

}
