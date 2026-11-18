package com.baomidou.mybatis3.mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import com.baomidou.mybatis3.domain.Blog;

public interface BlogChildBMapper extends BlogParentMapper {


    @ResultMap("BaseResultMap")
    @Select("""
        <script>
        select age
        from t_blog
        <where>
        <if test="id != null">
        id = #{id,jdbcType=NUMERIC}
        </if>
        </where>
        </script>""")
    List<Blog> selectAgeById(@Param("id") Long id);

}
