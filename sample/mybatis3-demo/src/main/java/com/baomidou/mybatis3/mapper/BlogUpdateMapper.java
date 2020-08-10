package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface BlogUpdateMapper extends BaseMapper<Blog> {
    // TODO CDATA
    int updateAgeByIdAndAgeAfter(@Param("age") Integer age,@Param("id") Long id, @Param("oldAge") Integer oldAge);
    // TODO CDATA
    int updateAgeAndContentByIdIn(@Param("age") Integer age, @Param("content") String content, @Param("idList") Collection<Long> idList);
    // TODO CDATA
    int updateAgeAndContentByIdNotIn(@Param("age") Integer age, @Param("content") String content, @Param("idList") Collection<Long> idList);

    int updateAgeAndContentByIdBetween(@Param("age")Integer age,@Param("content")String content,@Param("beginId")Long beginId,@Param("endId")Long endId);

    int updateAgeAndContentByIdLike(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdNotLike(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdStartWith(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdEndWith(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdContaining(@Param("age") Integer age, @Param("content") String content, @Param("id") Long id);

    int updateAgeAndContentByIdTrue(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdFalse(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdIgnoreCase(@Param("age") Integer age, @Param("content") String content, @Param("id") Long id);
    // TODO CDATA
    int updateAgeAndContentByIdGreaterThan(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);
    // TODO CDATA
    int updateAgeAndContentByIdGreaterThanEqual(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdIs(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);
}
