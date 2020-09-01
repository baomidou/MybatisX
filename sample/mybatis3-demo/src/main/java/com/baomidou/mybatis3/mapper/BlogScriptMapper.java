package com.baomidou.mybatis3.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;


import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

public interface BlogScriptMapper extends BaseMapper<Blog> {

    @Select("select * from t_blog where age = #{age,jdbcType=NUMERIC}")
    @ResultMap("BaseResultMap")
    List<Blog> selectAllByAge(@Param("age")Integer age);



    @Select("<script>" +
        "select * from t_blog" +
        " <where>" +
        "<if test=\"title!=null\">" +
        " title = #{title,jdbcType=NUMERIC}" +
        "</if>" +
        "</where>" +
        "</script>")
    @ResultMap("BaseResultMap")
    List<Blog> selectAllByTitle(@Param("title")String title);
}
