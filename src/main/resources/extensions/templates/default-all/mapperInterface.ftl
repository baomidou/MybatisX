package ${mapperInterface.packageName};

import ${tableClass.fullClassName};

/**
 * @Entity ${tableClass.fullClassName}
 */
public interface ${mapperInterface.fileName} {

    int deleteByPrimaryKey(Long id);

    int insert(${tableClass.shortClassName} record);

    int insertSelective(${tableClass.shortClassName} record);

    ${tableClass.shortClassName} selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(${tableClass.shortClassName} record);

    int updateByPrimaryKey(${tableClass.shortClassName} record);

}
