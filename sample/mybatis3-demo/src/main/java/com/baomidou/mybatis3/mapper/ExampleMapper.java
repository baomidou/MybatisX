package com.baomidou.mybatis3.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatis3.alias.MyAliasBlog;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


public interface ExampleMapper extends BaseMapper<Blog> {
    List<Blog> selectByIdAndCreateTimeBetweenAndTitleContainingOrderByAge(@Param("id")Long id,@Param("beginCreateTime")Date beginCreateTime,@Param("endCreateTime")Date endCreateTime,@Param("title")String title);


    List<Blog> selectByAgeAndTitleIn(@Param("age")Integer age,@Param("titleList")Collection<String> titleList);


    List<Blog> selectByAgeAndTitle(@Param("age")Integer age,@Param("title")String title);


    List<Blog> selectByAgeAndTitleAndContentBetween(@Param("age")Integer age,@Param("title")String title,@Param("beginContent")String beginContent,@Param("endContent")String endContent);


    MyAliasBlog selectBlogById(Long id);

}
