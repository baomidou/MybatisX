package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.domain.PerfDomain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface PerfDomainMapper extends BaseMapper<PerfDomain> {

    /**
     * 测试  100 个字段下的提示, 不卡顿
     *
     * @param title
     * @param appear
     * @return
     */
    List<PerfDomain> selectAllByTitleAfterAndAppearAfterOrderByAgeDescAndDown(@Param("title") String title, @Param("appear") String appear);


}
