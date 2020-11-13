package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;

public interface BlogDeleteMapper extends BaseMapper<Blog> {

    int delById(Long id);

    int delByIdAndAgeAfter(@Param("id")Long id,@Param("age")Integer age);

    int delByIdIn(@Param("idList")Collection<Long> idList);

    int delByIdNotIn(@Param("idList")Collection<Long> idList);

    int delByIdBetween(@Param("beginId")Long beginId,@Param("endId")Long endId);

    int delByTitleLike(@Param("title")String title);

    int delByTitleNotLike(@Param("title")String title);

    int delByTitleStartWith(@Param("title")String title);

    int delByTitleEndWith(@Param("title")String title);

    int delByTitleContaining(@Param("title")String title);

    int delByAgeTrue();

    int delByAgeFalse();

    int delByTitleIgnoreCase(@Param("title")String title);

    int delByTitleIs(@Param("title")String title);

    int delByIdGreaterThan(@Param("id")Long id);

    int delByIdGreaterThanEqual(@Param("id")Long id);

    int delByIdLessThan(@Param("id")Long id);
    // 测试用的,不是生成的
    int deleteAll();

}
