package com.baomidou.mybatis3.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatis3.domain.JpaBlog;

/**
 *
 */
public interface BlogCustomBaseMapper extends CustomBaseMapper<Integer,JpaBlog> {

    List<JpaBlog> selectByIdAndTitle(@Param("id") Long id, @Param("title") String title);
    List<JpaBlog> selectByAgeAndContentOrderByAge(@Param("age")Integer age,@Param("content")String content);


}
