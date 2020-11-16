package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.alias.MyAliasBlog;
import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public interface ExampleMapper extends BaseMapper<Blog> {
    int updateIdAndContentByAllFields(@Param("id") Long id, @Param("content") String content);

    int updateIdAndContentByALLFields(@Param("id") Long id, @Param("content") String content);
}
