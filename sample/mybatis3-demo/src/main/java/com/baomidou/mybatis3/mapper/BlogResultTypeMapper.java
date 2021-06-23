package com.baomidou.mybatis3.mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支持以 resultMap 的形式提示
 */
public interface BlogResultTypeMapper {

    /* 基础类型验证*/
    Byte countByAge(@Param("age") Integer age);

    Short countByAgeA(@Param("age") Integer age);

    Boolean countByAgeB(@Param("age") Integer age);

    Integer countByAgeC(@Param("age") Integer age);

    Long countByAgeD(@Param("age") Integer age);

    Float countByAgeE(@Param("age") Integer age);

    Double countByAgeF(@Param("age") Integer age);

    /* 数组类型的验证 */
    byte[] selectBlobTextById(int id);
}
