package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

public interface BlogInsertMapper extends BaseMapper<Blog> {

    int insertAll(@Param("blogCollection") Collection<Blog> blogCollection);

    //TODO CDATA
    int insertSelective(@Param("blogCollection") Collection<Blog> blogCollection);

    //TODO CDATA
    int insertBatch(@Param("blogCollection") Collection<Blog> blogCollection);


}
