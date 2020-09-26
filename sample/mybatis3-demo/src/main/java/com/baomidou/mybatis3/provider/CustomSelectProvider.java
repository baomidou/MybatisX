package com.baomidou.mybatis3.provider;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class CustomSelectProvider {

    public String selectByPrimaryKey(Map<String, Object> param){
        return new SQL(){{
            SELECT("*");
            FROM("t_blog");
            WHERE("id="+param.get("id"));
        }}.toString();
    }
}
