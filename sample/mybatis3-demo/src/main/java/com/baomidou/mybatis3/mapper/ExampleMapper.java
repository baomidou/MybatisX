package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.alias.MyAliasBlog;
import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public interface ExampleMapper extends BaseMapper<Blog> {
    List<Blog> selectByIdAndCreateTimeBetweenAndTitleContainingOrderByAge(@Param("id") Long id, @Param("beginCreateTime") Date beginCreateTime, @Param("endCreateTime") Date endCreateTime, @Param("title") String title);


    List<Blog> selectByAgeAndTitleIn(@Param("age") Integer age, @Param("titleList") Collection<String> titleList);


    List<Blog> selectByAgeAndTitle(@Param("age") Integer age, @Param("title") String title);


    List<Blog> selectByAgeAndTitleAndContentBetween(@Param("age") Integer age, @Param("title") String title, @Param("beginContent") String beginContent, @Param("endContent") String endContent);


    MyAliasBlog selectBlogById(Long id);

    List<Blog> selectIdAndContentById(@Param("id") Long id);

    /**
     * 用于测试jpa提示,  结果集区域的createTime不在参数中， xml默认值
     *
     * @param createTime
     * @return
     */
    int updateCreateTimeByCreateTime(@Param("createTime") Date createTime);

    /**
     * 用于测试 update set 字段=字段值  这里的换行
     *
     * @param content
     * @param title
     * @param money
     * @param id
     * @param createTime
     * @return
     */
    int updateContentAndCreateTimeAndTitleAndMoneyByIdAndCreateTime(@Param("content") String content, @Param("title") String title, @Param("money") BigDecimal money, @Param("id") Long id, @Param("createTime") Date createTime);


    int updateBlobTextByIdAndBlobTextIsNull(@Param("blobText") byte[] blobText, @Param("id") Long id);

    int updateBlobTextByIdAndBlobTextIn(@Param("blobText") byte[] blobText, @Param("id") Long id, @Param("oldblobText") byte[] oldblobText);

    List<Blog> selectBlobTextAndAgeAndCreateTimeByIdsInOrderByAge(@Param("ids") List<Integer> ids);

    int delByIdAndAge(@Param("id") Long id, @Param("age") Integer age);

    int updateMoneyAndAgeByMoneyAndContent(@Param("money") BigDecimal money, @Param("age") Integer age, @Param("oldmoney") BigDecimal oldmoney, @Param("content") String content);
}
