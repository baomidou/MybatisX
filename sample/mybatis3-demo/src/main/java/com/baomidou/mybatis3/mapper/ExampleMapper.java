package com.baomidou.mybatis3.mapper;
import com.baomidou.mybatis3.domain.BlogTitleContentDTO;
import com.baomidou.mybatis3.domain.BlogAgeContentDTO;
import com.baomidou.mybatis3.domain.BlogIdTitleDTO;

import java.util.List;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface ExampleMapper extends BaseMapper<Blog> {
    int updateIdAndContentByAllFields(@Param("id") Long id, @Param("content") String content);

    int updateIdAndContentByALLFields(@Param("id") Long id, @Param("content") String content);


    List<BlogIdTitleDTO> selectIdAndTitleById(@Param("id") Long id);

    List<BlogAgeContentDTO> selectAgeAndContentById(@Param("id") Long id);

    Blog insertAll(Blog blog);



    List<BlogTitleContentDTO> selectTitleAndContentByTitle(@Param("title") String title);
//    insertAll
//        updateAgeAndContentByMoneyAndCreateTime
    List<BlogTitleContentDTO> selectTitleAndContentById(@Param("id") Long id);
}
