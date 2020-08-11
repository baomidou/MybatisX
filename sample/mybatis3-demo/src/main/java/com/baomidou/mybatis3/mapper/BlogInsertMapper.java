package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * insert 只支持这三种提示和代码生成
 */
public interface BlogInsertMapper extends BaseMapper<Blog> {

    int insertAll(Blog blog);

    int insertSelective(Blog blog);

    int insertBatch(@Param("blogCollection")Collection<Blog> blogCollection);
}
