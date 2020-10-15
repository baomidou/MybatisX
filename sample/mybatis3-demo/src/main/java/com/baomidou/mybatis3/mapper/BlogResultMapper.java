package com.baomidou.mybatis3.mapper;
import java.util.Date;
import java.util.Collection;
import java.math.BigDecimal;
import java.util.List;
import com.baomidou.mybatis3.domain.Blog;
import org.apache.ibatis.annotations.Param;

/**
 * 支持以 resultMap 的形式提示
 */
public interface BlogResultMapper {

    /**
     * 测试一长串的提示性能
     *
     * @param age
     * @param title
     * @param beginCreateTime
     * @param endCreateTime
     * @param idList
     * @return
     */
    List<Blog> selectAllByAgeAndTitleContainingAndCreateTimeBetweenAndIdInOrderByCreateTimeDesc(@Param("age") Integer age, @Param("title") String title, @Param("beginCreateTime") Date beginCreateTime, @Param("endCreateTime") Date endCreateTime, @Param("idList") Collection<Long> idList);


}
