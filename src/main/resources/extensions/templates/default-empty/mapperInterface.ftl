package ${mapperInterface.packageName};

import ${tableClass.fullClassName};

/**
* @author ${author!}
* @description 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Mapper
* @createDate ${.now?string('yyyy-MM-dd HH:mm:ss')}
* @Entity ${tableClass.fullClassName}
*/
public interface ${mapperInterface.fileName} {

}




