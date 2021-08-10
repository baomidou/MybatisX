package com.baomidou.mybatis3.test;

import com.baomidou.mybatis3.MybatisPlus3Application;
import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatis3.mapper.BlogDeleteMapper;
import com.baomidou.mybatis3.mapper.BlogInsertMapper;
import com.baomidou.mybatis3.mapper.BlogUpdateMapper;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlus3Application.class)
public class BlogUpdateMapperTest {

    @Resource
    BlogUpdateMapper blogUpdateMapper;
    @Test
    public void updateAgeAndContentByIdIn() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdIn(50, "content-new", Arrays.asList(1L, 3L, 4L, 10L));
        Assert.assertEquals(changeCount,3);
    }
    @Test
    public void updateAgeAndContentByIdNotIn() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdNotIn(50, "content-new", Arrays.asList(1L, 3L, 4L, 10L));
        Assert.assertEquals(changeCount,2);
    }
    @Test
    public void updateAgeAndContentByIdBetween() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdBetween(50, "content-new", 2L, 5L);
        Assert.assertEquals(changeCount,4);
    }
    @Test
    public void updateAgeAndContentByIdLike() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdLike(50, "content-new", 3L);
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeAndContentByIdNotLike() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByTitleNotLike(50, "content-new", "baomidou-a");
        Assert.assertEquals(changeCount,4);
    }
    @Test
    public void updateAgeAndContentByIdStartWith() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByTitleStartWith(50, "content-new", "baomidou");
        Assert.assertEquals(changeCount,2);
    }
    @Test
    public void updateAgeAndContentByIdEndWith() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByTitleEndWith(50, "content-new", "-a");
        Assert.assertEquals(changeCount,3);
    }
    @Test
    public void updateAgeAndContentByIdContaining() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByTitleContaining(50, "content-new", "baomidou");
        Assert.assertEquals(changeCount,2);
    }
    @Test
    public void updateAgeAndContentByIdTrue() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByAgeTrue(50, "content-new");
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeAndContentByIdFalse() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByAgeFalse(50, "content-new");
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeAndContentByIdIgnoreCase() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByTitleIgnoreCase(50, "content-new", "apaCHE-A");
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeAndContentByIdGreaterThan() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdGreaterThan(50, "content-new", 4L);
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeAndContentByIdGreaterThanEqual() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdGreaterThanEqual(50, "content-new", 4L);
        Assert.assertEquals(changeCount,2);
    }
    @Test
    public void updateAgeAndContentByIdIs() {
        int changeCount = blogUpdateMapper.updateAgeAndContentByIdIs(50, "content-new",4L);
        Assert.assertEquals(changeCount,1);
    }
    @Test
    public void updateAgeByIdAndAgeAfter() {
        int changeCount = blogUpdateMapper.updateAgeByAgeAfter(50,20);
        Assert.assertEquals(changeCount,3);
    }
    @Test
    public void updateSelective() {
        Blog blog = new Blog();
        blog.setId(3L);
        blog.setAge(30);
        blog.setContent("haha");
        int changeCount = blogUpdateMapper.updateSelective(blog);
        Assert.assertEquals(changeCount,1);
    }

    @Resource
    BlogInsertMapper blogInsertMapper;


    @After
    public void destroyData(){
        blogDeleteMapper.deleteAll();
    }
    @Before
    public void initData(){
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
        blogB.setAge(23);
        blogB.setMoney(BigDecimal.valueOf(5500));
        blogB.setCreateTime(new Date());
        blogInsertMapper.insertAll(blogB);

        Blog blogD = new Blog();
        blogD.setId(3L);
        blogD.setTitle("baomidou-a");
        blogD.setContent("content-a");
        blogD.setAge(23);
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


    @Resource
    private BlogDeleteMapper blogDeleteMapper;



}
