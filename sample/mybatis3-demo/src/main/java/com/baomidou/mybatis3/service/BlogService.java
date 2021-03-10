package com.baomidou.mybatis3.service;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatis3.mapper.BlogSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

public class BlogService {

    @Resource
    private BlogSelectMapper blogSelectMapper;

    @Autowired
    private BlogSelectMapper blogSelectMapper2;

    public List<Blog> selectByAgeIs(Integer age) {
        return blogSelectMapper.selectByAgeIs(age);
    }
}
