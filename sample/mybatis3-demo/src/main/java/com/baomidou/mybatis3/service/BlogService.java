package com.baomidou.mybatis3.service;

import com.baomidou.mybatis3.mapper.BlogSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class BlogService {

    @Resource
    private BlogSelectMapper blogSelectMapper;

    @Autowired
    private BlogSelectMapper blogSelectMapper2;


}
