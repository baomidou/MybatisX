package com.baomidou.mybatis2.test;

import com.baomidou.mybatis2.MybatisPlus2Application;
import com.baomidou.mybatis2.domain.Blog;
import com.baomidou.mybatis2.mapper.BlogDeleteMapper;
import com.baomidou.mybatis2.mapper.BlogInsertMapper;
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
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlus2Application.class)
public class BlogDeleteMapperTest {

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


    @Test
    public void delByIdAndAgeAfter() {
        int noChange = blogDeleteMapper.delByIdAndAgeAfter(1L, 50);
        int hasChange = blogDeleteMapper.delByIdAndAgeAfter(1L, 20);
        Assert.assertEquals(noChange, 0);
        Assert.assertEquals(hasChange, 1);
    }

    @Test
    public void delByIdIn() {
        int changeCount = blogDeleteMapper.delByIdIn(Arrays.asList(2L, 3L, 4L));
        Assert.assertEquals(changeCount, 3);
    }

    @Test
    public void delByIdNotIn() {
        int changeCount = blogDeleteMapper.delByIdNotIn(Arrays.asList(2L, 3L, 4L));
        Assert.assertEquals(changeCount, 2);
    }
    @Test
    public void delByIdBetween() {
        int changeCount =  blogDeleteMapper.delByIdBetween(2L, 4L);
        Assert.assertEquals(changeCount, 3);
    }
    @Test
    public void delByIdNotLike() {
        int hasChange = blogDeleteMapper.delByTitleLike("title-a");
        Assert.assertEquals(hasChange, 1);
        int noChange = blogDeleteMapper.delByTitleLike("title");
        Assert.assertEquals(noChange, 0);
    }
    @Test
    public void delByIdStartWith() {
        int changeCount = blogDeleteMapper.delByTitleStartWith("-a");
        Assert.assertEquals(changeCount, 3);
    }
    @Test
    public void delByIdEndWith() {
        int changeCount = blogDeleteMapper.delByTitleEndWith("baomidou");
        Assert.assertEquals(changeCount, 2);
    }
    @Test
    public void delByIdContaining() {
        int changeCount = blogDeleteMapper.delByTitleContaining("dou");
        Assert.assertEquals(changeCount, 2);
    }
    @Test
    public void delByAgeTrue() {
        int changeCount = blogDeleteMapper.delByAgeTrue();
        Assert.assertEquals(changeCount, 1);
    }
    @Test
    public void delByAgeFalse() {
        int changeCount = blogDeleteMapper.delByAgeFalse();
        Assert.assertEquals(changeCount, 1);
    }
    @Test
    public void delByTitleIgnoreCase() {
        int changeCount = blogDeleteMapper.delByTitleIgnoreCase("apaCHE-A");
        Assert.assertEquals(changeCount, 1);
    }
    @Test
    public void delByTitleIs() {
        int changeCount = blogDeleteMapper.delByTitleIs("apache-a");
        Assert.assertEquals(changeCount, 1);
    }
    @Test
    public void delByIdGreaterThan() {
        int changeCount = blogDeleteMapper.delByIdGreaterThan(3L);
        Assert.assertEquals(changeCount, 2);
    }
    @Test
    public void delByIdLessThan() {
        int changeCount = blogDeleteMapper.delByIdLessThan(4L);
        Assert.assertEquals(changeCount, 3);
    }

    @Resource
    private BlogDeleteMapper blogDeleteMapper;

    /**
     * like 没有百分号
     */
    @Test
    public void delByTitleLike() {
        int changeCount = blogDeleteMapper.delByTitleLike("title");
        Assert.assertEquals(changeCount, 0);
    }

}
