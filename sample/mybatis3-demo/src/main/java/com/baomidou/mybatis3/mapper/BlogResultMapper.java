package com.baomidou.mybatis3.mapper;
import java.util.List;
import com.baomidou.mybatis3.domain.Blog;
import org.apache.ibatis.annotations.Param;

/**
 * 支持以 resultMap 的形式提示
 */
public interface BlogResultMapper {

    List<Blog> selectIdAndAgeByIdLessThanOrderByIdDesc(@Param("id")Long id);

}
