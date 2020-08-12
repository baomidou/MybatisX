package com.baomidou.mybatis2.test;

import com.baomidou.mybatis2.MybatisPlus2Application;
import com.baomidou.mybatis2.domain.Blog;
import com.baomidou.mybatis2.mapper.BlogInsertMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlus2Application.class)
public class BlogInsertMapperTest {

    @Resource
    private BlogInsertMapper blogInsertMapper;

    @Test
    public void testInsertAll() {
        Blog blog = buildBean(1L);
        int changeCount = blogInsertMapper.insertAll(blog);
        Assert.assertEquals(changeCount, 1);
    }


    @Test
    public void testInsertSelective() {
        Blog blog = buildBean(20L);
        blog.setContent(null);
        blog.setAge(null);
        int changeCount = blogInsertMapper.insertSelective(blog);
        Assert.assertEquals(changeCount, 1);
    }


    @Test
    public void testInsertBatch() {
        Blog blogA = buildBean(100L);
        Blog blogB = buildBean(101L);
        Blog blogC = buildBean(102L);
        int changeCount = blogInsertMapper.insertBatch(Arrays.asList(blogA,blogB,blogC));
        Assert.assertEquals(changeCount, 3);
    }

    private Blog buildBean(long id) {
        Blog blog = new Blog();
        blog.setId(id);
        blog.setAge(23);
        blog.setTitle("title-a");
        blog.setContent("this is a content");
        blog.setMoney(new BigDecimal("5.82"));
        blog.setCreateTime(new Date());
        return blog;
    }
}
