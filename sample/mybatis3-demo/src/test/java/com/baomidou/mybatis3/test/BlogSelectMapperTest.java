package com.baomidou.mybatis3.test;

import com.baomidou.mybatis3.MybatisPlus3Application;
import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatis3.mapper.BlogDeleteMapper;
import com.baomidou.mybatis3.mapper.BlogInsertMapper;
import com.baomidou.mybatis3.mapper.BlogSelectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlus3Application.class)
public class BlogSelectMapperTest {

    @Resource
    BlogInsertMapper blogInsertMapper;

    @Resource
    BlogSelectMapper blogSelectMapper;

    @After
    public void destroyData() {
        blogDeleteMapper.deleteAll();
    }

    @Before
    public void initData() {
        Blog blogA = new Blog();
        blogA.setId(1L);
        blogA.setTitle("title-a");
        blogA.setContent("content-a");
        blogA.setAge(23);
        blogA.setMoney(BigDecimal.valueOf(5500));
        blogA.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogA);

        Blog blogB = new Blog();
        blogB.setId(2L);
        blogB.setTitle("title-b");
        blogB.setContent("content-a");
        blogB.setAge(30);
        blogB.setMoney(BigDecimal.valueOf(5500));
        blogB.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogB);

        Blog blogD = new Blog();
        blogD.setId(3L);
        blogD.setTitle("baomidou-a");
        blogD.setContent("content-a");
        blogD.setAge(40);
        blogD.setMoney(BigDecimal.valueOf(5500));
        blogD.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogD);


        Blog blogE = new Blog();
        blogE.setId(4L);
        blogE.setTitle("baomidou-b");
        blogE.setContent("content-a");
        blogE.setAge(0);
        blogE.setMoney(BigDecimal.valueOf(5500));
        blogE.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogE);

        Blog blogF = new Blog();
        blogF.setId(5L);
        blogF.setTitle("apache-a");
        blogF.setContent("content-a");
        blogF.setAge(1);
        blogF.setMoney(BigDecimal.valueOf(5500));
        blogF.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogF);
    }

    @Test
    public void selectByIdAndAgeAfter() {
        List<Blog> blogs = blogSelectMapper.selectByIdAndAgeAfter(1L, 20);
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByIdIn() {
        List<Blog> blogs = blogSelectMapper.selectByIdIn(Arrays.asList(1L, 3L, 10L));
        Assert.assertEquals(blogs.size(), 2);
    }

    @Test
    public void selectByIdNotIn() {
        List<Blog> blogs = blogSelectMapper.selectByIdNotIn(Arrays.asList(1L, 3L, 10L));
        Assert.assertEquals(blogs.size(), 3);
    }

    @Test
    public void selectByAgeBetween() {
        List<Blog> blogs = blogSelectMapper.selectByAgeBetween(20, 40);
        Assert.assertEquals(blogs.size(), 3);
    }

    @Test
    public void selectByIdLike() {
        List<Blog> blogs = blogSelectMapper.selectByIdLike(1L);
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByIdNotLike() {
        List<Blog> blogs = blogSelectMapper.selectByIdNotLike(1L);
        Assert.assertEquals(blogs.size(), 4);
    }

    @Test
    public void selectByIdStartWith() {
        List<Blog> blogs = blogSelectMapper.selectByIdStartWith(1L);
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByTitleEndWith() {
        List<Blog> blogs = blogSelectMapper.selectByTitleEndWith("baomidou");
        Assert.assertEquals(blogs.size(), 2);
    }

    @Test
    public void selectByIdContaining() {
        List<Blog> blogs = blogSelectMapper.selectByTitleContaining("midou");
        Assert.assertEquals(blogs.size(), 2);
    }

    @Test
    public void selectByIdTrue() {
        List<Blog> blogs = blogSelectMapper.selectByAgeTrue();
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByIdFalse() {
        List<Blog> blogs = blogSelectMapper.selectByAgeFalse();
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByTitleIgnoreCase() {
        List<Blog> blogs = blogSelectMapper.selectByTitleIgnoreCase("aPacHE-A");
        Assert.assertEquals(blogs.size(), 1);
    }

    @Test
    public void selectByAgeGreaterThan() {
        List<Blog> blogs = blogSelectMapper.selectByAgeGreaterThan(25);
        Assert.assertEquals(blogs.size(), 2);
    }

    @Test
    public void selectByAgeGreaterThanEqual() {
        List<Blog> blogs = blogSelectMapper.selectByAgeGreaterThanEqual(23);
        Assert.assertEquals(blogs.size(), 3);
    }


    @Test
    public void selectByAgeLessThan() {
        List<Blog> blogs = blogSelectMapper.selectByAgeLessThan(30);
        Assert.assertEquals(blogs.size(), 3);
    }

    @Test
    public void selectByAgeLessThanEqual() {
        List<Blog> blogs = blogSelectMapper.selectByAgeLessThanEqual(30);
        Assert.assertEquals(blogs.size(), 4);
    }

    @Test
    public void selectByAgeIs() {
        List<Blog> blogs = blogSelectMapper.selectByAgeIs(23);
        Assert.assertEquals(blogs.size(), 1);
    }

    @Resource
    private BlogDeleteMapper blogDeleteMapper;


}
