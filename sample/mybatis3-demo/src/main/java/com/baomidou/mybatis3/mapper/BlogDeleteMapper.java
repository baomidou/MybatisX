package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface BlogDeleteMapper extends BaseMapper<Blog> {

    int delByIdAndAgeAfter(@Param("id")Long id,@Param("age")Integer age);

    int delByIdIn(@Param("idList")Collection<Long> idList);

    int delByIdNotIn(@Param("idList")Collection<Long> idList);

    int delByIdBetween(@Param("beginId")Long beginId,@Param("endId")Long endId);

    int delByIdLike(@Param("id")Long id);

    int delByIdNotLike(@Param("id")Long id);

    int delByIdStartWith(@Param("id")Long id);

    int delByIdEndWith(@Param("id")Long id);

    int delByIdContaining(@Param("id")Long id);

    int delByIdTrue(@Param("id")Long id);

    int delByIdFalse(@Param("id")Long id);

    int delByIdIgnoreCase(@Param("id")Long id);

    int delByIdIs(@Param("id")Long id);

    int delByIdGreaterThan(@Param("id")Long id);

    int delByIdGreaterThanEqual(@Param("id")Long id);
}
