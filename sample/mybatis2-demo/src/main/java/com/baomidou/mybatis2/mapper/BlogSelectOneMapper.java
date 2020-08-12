package com.baomidou.mybatis2.mapper;

import java.util.Date;

import com.baomidou.mybatis2.domain.Blog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BlogSelectOneMapper extends BaseMapper<Blog> {

    Blog selectOneById(@Param("id") Long id);

    // 假设 title 是唯一索引
    Blog selectOneByTitle(@Param("title") String title);

    // 假设 content + age 是联合唯一索引
    Blog selectOneByContentAndAge(@Param("content") String content, @Param("age") Integer age);

    List<Blog> selectAllByTitle(@Param("title") String title);

    List<Blog> selectAllByCreateTimeBetween(@Param("beginCreateTime") Date beginCreateTime, @Param("endCreateTime") Date endCreateTime);


}
