package com.baomidou.mybatis3.mapper;

import com.baomidou.mybatis3.alias.QueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liushang
 * @version 1.0
 * @description: TODO
 * @date 2026/11/5 17:01
 * @copyright Shenzhen DTB RFID Co., Ltd  All rights reserved.
 **/

@Mapper
public interface ShareSqlExampleMapper {
    List<?> selectList(@Param("param") QueryDTO param);
}
