package com.baomidou.mybatis3.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface BlogSelectMapper extends BaseMapper<Blog> {

    List<Blog> selectByIdAndAgeAfter(@Param("id") Long id, @Param("age") Integer age);

    List<Blog> selectByIdIn(@Param("idList") Collection<Long> idList);

    List<Blog> selectByIdNotIn(@Param("idList") Collection<Long> idList);

    List<Blog> selectByAgeBetween(@Param("beginAge") Integer beginAge, @Param("endAge") Integer endAge);

    List<Blog> selectByIdLike(@Param("id") Long id);

    List<Blog> selectByIdNotLike(@Param("id") Long id);

    List<Blog> selectByIdStartWith(@Param("id") Long id);

    List<Blog> selectByTitleEndWith(@Param("title") String title);

    List<Blog> selectByTitleContaining(@Param("title") String title);

    List<Blog> selectByAgeTrue();

    List<Blog> selectByAgeFalse();

    List<Blog> selectByTitleIgnoreCase(@Param("title") String title);

    List<Blog> selectByAgeGreaterThan(@Param("age") Integer age);

    List<Blog> selectByAgeGreaterThanEqual(@Param("age") Integer age);

    List<Blog> selectByAgeLessThan(@Param("age") Integer age);

    List<Blog> selectByAgeLessThanEqual(@Param("age") Integer age);

    List<Blog> selectByAgeIs(@Param("age") Integer age);

    List<Blog> selectByAgeAndTitle(@Param("age")Integer age,@Param("title")String title);

    List<Blog> selectIdAndTitleAndCreateTimeAndContentById(@Param("id")Long id);
}
