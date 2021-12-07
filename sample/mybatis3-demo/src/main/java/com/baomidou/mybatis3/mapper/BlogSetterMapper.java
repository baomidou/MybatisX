package com.baomidou.mybatis3.mapper;
import com.baomidou.mybatis3.domain.BlogSetterDO;
import java.util.List;

import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * insert 只支持这三种提示和代码生成
 */
public interface BlogSetterMapper  {


    BlogSetterDO selectOneById(@Param("id") Integer id);
}
