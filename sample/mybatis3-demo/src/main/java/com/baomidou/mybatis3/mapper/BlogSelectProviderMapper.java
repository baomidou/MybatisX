package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatis3.provider.CustomSelectProvider;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Collection;
import java.util.List;


public interface BlogSelectProviderMapper  {

    @SelectProvider(value = CustomSelectProvider.class, method = "selectByPrimaryKey")
    Blog selectByPrimaryKey(@Param("id") Long id);


}
