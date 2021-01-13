package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.Blog;
import org.apache.ibatis.annotations.Param;

public interface BlogTipMapper {
    /**
     *
     * @param id 主键id
     * @return 博客名称
     */
    Blog selectByKey(@Param("id") Long id);

    /**
     * 通过id更新
     * @param blog blog请求对象
     * @return 更新的行数
     */
    int updateById(@Param("blog") Blog blog);
}
