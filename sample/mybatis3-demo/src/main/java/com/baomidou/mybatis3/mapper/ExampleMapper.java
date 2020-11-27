package com.baomidou.mybatis3.mapper;
import java.util.List;
import com.baomidou.mybatis3.domain.BlogAgeContentDTO;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface ExampleMapper extends BaseMapper<Blog> {
    int updateIdAndContentByAllFields(@Param("id") Long id, @Param("content") String content);

    int updateIdAndContentByALLFields(@Param("id") Long id, @Param("content") String content);

    selectAgeAndContentById

}
