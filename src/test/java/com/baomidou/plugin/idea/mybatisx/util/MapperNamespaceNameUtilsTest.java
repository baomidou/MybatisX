package com.baomidou.plugin.idea.mybatisx.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class MapperNamespaceNameUtilsTest {

    @Test
    public void testExtractBaseName() {
        Assert.assertEquals("UserMapper", MapperNamespaceNameUtils.extractBaseName("UserMapper.xml"));
        Assert.assertEquals("UserMapper", MapperNamespaceNameUtils.extractBaseName("UserMapper.XML"));
        Assert.assertEquals("User", MapperNamespaceNameUtils.extractBaseName("User.mapper.xml"));
        Assert.assertEquals("sys_user_dao", MapperNamespaceNameUtils.extractBaseName("sys_user_dao.xml"));
    }

    @Test
    public void testToCamelCase() {
        Assert.assertEquals("SysUserMapper", MapperNamespaceNameUtils.toCamelCase("sys_user_mapper"));
        Assert.assertEquals("SysUserDao", MapperNamespaceNameUtils.toCamelCase("sys-user-dao"));
        Assert.assertEquals("UserMapper", MapperNamespaceNameUtils.toCamelCase("UserMapper"));
    }

    @Test
    public void testExtractTokensWithoutStopWords() {
        // 禁止 STOP_WORDS：UserMapper 应完整分词为 [User, Mapper]，不丢弃 Mapper
        List<String> tokens1 = MapperNamespaceNameUtils.extractTokens("UserMapper");
        Assert.assertEquals(2, tokens1.size());
        Assert.assertEquals("User", tokens1.get(0));
        Assert.assertEquals("Mapper", tokens1.get(1));

        // SysUserDao 应完整分词为 [Sys, User, Dao]，不丢弃 Dao
        List<String> tokens2 = MapperNamespaceNameUtils.extractTokens("SysUserDao");
        Assert.assertEquals(3, tokens2.size());
        Assert.assertEquals("Sys", tokens2.get(0));
        Assert.assertEquals("User", tokens2.get(1));
        Assert.assertEquals("Dao", tokens2.get(2));

        // 下划线命名
        List<String> tokens3 = MapperNamespaceNameUtils.extractTokens("sys_user_mapper");
        Assert.assertEquals(3, tokens3.size());
        Assert.assertEquals("sys", tokens3.get(0));
        Assert.assertEquals("user", tokens3.get(1));
        Assert.assertEquals("mapper", tokens3.get(2));
    }

    @Test
    public void testGetCandidateClassNamesWithoutSuffixVariants() {
        // 禁止去通用后缀后的变体匹配（如 UserDao.xml 对应 UserMapper）
        Set<String> candidatesDao = MapperNamespaceNameUtils.getCandidateClassNames("UserDao.xml");
        Assert.assertTrue(candidatesDao.contains("UserDao"));
        Assert.assertFalse(candidatesDao.contains("UserMapper"));

        Set<String> candidatesMapper = MapperNamespaceNameUtils.getCandidateClassNames("UserMapper.xml");
        Assert.assertTrue(candidatesMapper.contains("UserMapper"));
        Assert.assertFalse(candidatesMapper.contains("UserDao"));

        Set<String> candidatesUnderline = MapperNamespaceNameUtils.getCandidateClassNames("sys_user_mapper.xml");
        Assert.assertTrue(candidatesUnderline.contains("sys_user_mapper"));
        Assert.assertTrue(candidatesUnderline.contains("SysUserMapper"));
        Assert.assertFalse(candidatesUnderline.contains("SysUserDao"));
    }
}
