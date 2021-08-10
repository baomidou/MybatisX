package com.baomidou.mybatis3.mapper;
import java.util.List;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    /**
     * 当年龄为1时更新年龄和内容
     * @param content 内容
     * @param age 年龄
     * @return 更新的记录数
     */
    int updateAgeAndContentByAgeTrue(@Param("age")Integer age,@Param("content")String content);

    int updateAgeAndContentByAgeFalse(@Param("age")Integer age,@Param("content")String content);

    int updateSelective(Blog blog);
}
