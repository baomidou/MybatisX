package com.baomidou.mybatis3.mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatis3.domain.JpaBlog;
import org.apache.ibatis.annotations.*;

/**
 * @Entity com.baomidou.mybatis3.domain.JpaBlog
 */
public interface BlogAnnotationMapper {




    @ResultMap("BaseResultMap")
    @Select("<script>"
        + "select <include refid=\"Base_Column_List\"/> from t_blog"
        + "<where>"
        + "<if test=\"id != null\">"
        + "id = #{id,jdbcType=NUMERIC}"
        + "</if>"
        + "</where>"
        + "</script>")
    JpaBlog selectOneById(@Param("id") Long id);

    @Update("<script>"
        + "update t_blog"
        + " set title = #{title,jdbcType=VARCHAR}"
        + "<where>"
        + "<if test=\"id != null\">"
        + "id = #{id,jdbcType=NUMERIC}"
        + "</if>"
        + "</where>"
        + "</script>")
    int updateTitleById(@Param("title") String title, @Param("id") Long id);


    @Delete("<script>"
        + "delete from t_blog"
        + "<where>"
        + "<if test=\"id != null\">"
        + "id = #{id,jdbcType=NUMERIC}"
        + "</if>"
        + "</where>"
        + "</script>")
    int delById(@Param("id") Long id);

    @Insert("<script>"
        + "insert into t_blog"
        + "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\"><if test=\"id != null\">id,</if>"
        + "<if test=\"title != null\">title,</if>"
        + "<if test=\"content != null\">content,</if>"
        + "<if test=\"money != null\">money,</if>"
        + "<if test=\"age != null\">age,</if>"
        + "<if test=\"createTime != null\">create_time,</if></trim>"
        + "values"
        + "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\"><if test=\"id != null\">#{id,jdbcType=NUMERIC},</if>"
        + "<if test=\"title != null\">#{title,jdbcType=VARCHAR},</if>"
        + "<if test=\"content != null\">#{content,jdbcType=VARCHAR},</if>"
        + "<if test=\"money != null\">#{money,jdbcType=DECIMAL},</if>"
        + "<if test=\"age != null\">#{age,jdbcType=NUMERIC},</if>"
        + "<if test=\"createTime != null\">#{createTime,jdbcType=TIMESTAMP},</if>"
        + "</trim>"
        + "</script>")
    int insertSelective(JpaBlog jpaBlog);
}
