package com.baomidou.mybatis3.test;

import com.baomidou.mybatis3.MybatisPlus3Application;
import com.baomidou.mybatis3.alias.MyAliasBlog;
import com.baomidou.mybatis3.domain.Blog;
import com.baomidou.mybatis3.mapper.BlogDeleteMapper;
import com.baomidou.mybatis3.mapper.BlogInsertMapper;
import com.baomidou.mybatis3.mapper.BlogSelectMapper;
import com.baomidou.mybatis3.mapper.ExampleMapper;
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
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisPlus3Application.class)
public class BlogExampleMapperTest {

    @Resource
    BlogInsertMapper blogInsertMapper;

    @Resource
    ExampleMapper exampleMapper;

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
    public void selectByAgeIs() {
        MyAliasBlog blog = exampleMapper.selectBlogById(1L);
        Assert.assertNotNull(blog);
    }

    @Resource
    private BlogDeleteMapper blogDeleteMapper;


}
