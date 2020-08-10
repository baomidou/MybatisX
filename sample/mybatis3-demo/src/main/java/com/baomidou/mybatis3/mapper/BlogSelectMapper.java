package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

// TODO CDATA
public interface BlogSelectMapper extends BaseMapper<Blog> {

    Blog selectByIdAndAgeAfter(@Param("id") Long id, @Param("age") Integer age);

    Blog selectByIdIn(@Param("idList")Collection<Long> idList);

    Blog selectByIdNotIn(@Param("idList")Collection<Long> idList);

    Blog selectByIdBetween(@Param("beginId")Long beginId,@Param("endId")Long endId);

    Blog selectByIdLike(@Param("id")Long id);

    Blog selectByIdNotLike(@Param("id")Long id);

    Blog selectByIdStartWith(@Param("id")Long id);

    Blog selectByIdEndWith(@Param("id")Long id);

    Blog selectByIdContaining(@Param("id")Long id);

    Blog selectByIdTrue(@Param("id")Long id);

    Blog selectByIdFalse(@Param("id")Long id);

    Blog selectByIdIgnoreCase(@Param("id")Long id);

    Blog selectByIdGreaterThan(@Param("id")Long id);

    Blog selectByIdGreaterThanEqual(@Param("id")Long id);

    Blog selectByIdIs(@Param("id")Long id);


}
