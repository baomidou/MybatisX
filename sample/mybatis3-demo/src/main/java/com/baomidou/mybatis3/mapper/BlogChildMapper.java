package com.baomidou.mybatis3.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatis3.domain.Blog;

public interface BlogChildMapper extends BlogParentMapper {
    List<Blog> selectById(@Param("id") Long id);

}
