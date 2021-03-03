package com.baomidou.mybatis3.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatis3.domain.Blog;

/**
 *
 */
public interface BlogParentMapper   {
    List<Blog> selectByAge(@Param("age") Integer age);

}
