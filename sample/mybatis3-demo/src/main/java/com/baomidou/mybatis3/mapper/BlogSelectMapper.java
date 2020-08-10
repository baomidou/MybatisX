package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

// TODO CDATA
public interface BlogSelectMapper extends BaseMapper<Blog> {
    Blog selectByIdAndAgeAfter(@Param("id") Long id, @Param("age") Integer age);

    // TODO CDATA
    int selectByIdAndAgeAfter(@Param("id") Long id, @Param("age") Integer age);

    // TODO CDATA
    int selectByIdIn(@Param("idList") Collection<Long> idList);

    // TODO CDATA
    int selectByIdNotIn(@Param("idList") Collection<Long> idList);

    // TODO CDATA
    Blog selectByIdBetween(@Param("beginId") Long beginId, @Param("endId") Long endId);

    int selectByIdLike(@Param("id") Long id);

    int selectByIdNotLike(@Param("id") Long id);

    int selectByIdStartWith(@Param("id") Long id);

    int selectByIdEndWith(@Param("id") Long id);

    int selectByIdContaining(@Param("id") Long id);

    int selectByIdTrue(@Param("id") Long id);

    int selectByIdFalse(@Param("id") Long id);

    int selectByIdIgnoreCase(@Param("id") Long id);

    int selectByIdGreaterThan(@Param("id") Long id);

    int selectByIdGreaterThanEqual(@Param("id") Long id);

    int selectByIdIs(@Param("id") Long id);


}
