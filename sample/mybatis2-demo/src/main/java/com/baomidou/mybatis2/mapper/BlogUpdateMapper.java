package com.baomidou.mybatis2.mapper;

import com.baomidou.mybatis2.domain.Blog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface BlogUpdateMapper extends BaseMapper<Blog> {

    int updateAgeAndContentByIdIn(@Param("age")Integer age,@Param("content")String content,@Param("idList")Collection<Long> idList);

    int updateAgeAndContentByIdNotIn(@Param("age") Integer age, @Param("content") String content, @Param("idList") Collection<Long> idList);

    int updateAgeAndContentByIdBetween(@Param("age")Integer age,@Param("content")String content,@Param("beginId")Long beginId,@Param("endId")Long endId);

    int updateAgeAndContentByIdLike(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByTitleNotLike(@Param("age")Integer age,@Param("content")String content,@Param("title")String title);

    int updateAgeAndContentByTitleStartWith(@Param("age")Integer age,@Param("content")String content,@Param("title")String title);

    int updateAgeAndContentByTitleEndWith(@Param("age")Integer age,@Param("content")String content,@Param("title")String title);

    int updateAgeAndContentByTitleContaining(@Param("age")Integer age,@Param("content")String content,@Param("title")String title);

    int updateAgeAndContentByTitleIgnoreCase(@Param("age")Integer age,@Param("content")String content,@Param("title")String title);

    int updateAgeAndContentByIdGreaterThan(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdGreaterThanEqual(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);

    int updateAgeAndContentByIdIs(@Param("age")Integer age,@Param("content")String content,@Param("id")Long id);


    int updateAgeByAgeAfter(@Param("age")Integer age,@Param("oldAge")Integer oldAge);


    int updateAgeAndContentByAgeFalse(@Param("age")Integer age,@Param("content")String content);

    int updateAgeAndContentByAgeTrue(@Param("age")Integer age,@Param("content")String content);

}
