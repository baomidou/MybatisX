package ${baseInfo.packageName};

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import ${tableClass.fullClassName};
import ${serviceInterface.packageName}.${serviceInterface.fileName};
import ${mapperInterface.packageName}.${mapperInterface.fileName};
<#if baseService??&&baseService!="">
    import ${baseService};
    <#list baseService?split(".") as simpleName>
        <#if !simpleName_has_next>
            <#assign serviceSimpleName>${simpleName}</#assign>
        </#if>
    </#list>
</#if>
import org.springframework.stereotype.Service;

/**
*
*/
@Service
public class ${baseInfo.fileName} extends ServiceImpl<${mapperInterface.fileName}, ${tableClass.shortClassName}>
implements ${serviceInterface.fileName}{

}
