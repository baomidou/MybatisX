package com.baomidou.mybatis3.domain;

import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NestedDO {
    private Integer id;
    public class InnerClass implements org.apache.ibatis.type.TypeHandler{

        @Override
        public void setParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {

        }

        @Override
        public Object getResult(ResultSet resultSet, String s) throws SQLException {
            return null;
        }

        @Override
        public Object getResult(ResultSet resultSet, int i) throws SQLException {
            return null;
        }

        @Override
        public Object getResult(CallableStatement callableStatement, int i) throws SQLException {
            return null;
        }
    }

    public class InnerClassNotTypeHandler{

    }
}
