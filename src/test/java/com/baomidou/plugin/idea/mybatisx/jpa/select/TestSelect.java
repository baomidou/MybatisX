package com.baomidou.plugin.idea.mybatisx.jpa.select;

import com.baomidou.plugin.idea.mybatisx.jpa.BaseJpaTest;

import java.io.IOException;

/**
 * @author ls9527
 * <p>
 * JavaCompletionTestCase
 * <p>
 * JavaCodeInsightFixtureTestCase
 */
public class TestSelect extends BaseJpaTest {


    public void testSelectById() throws IOException {
        launchAction("selectById");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectById.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectById.xml",
            true);

    }



    public void testSelectByIdAndAgeAfter() throws IOException {


        launchAction("selectByIdAndAgeAfter");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdAndAgeAfter.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdAndAgeAfter.xml",
            true);

    }

    public void testSelectByIdIn() throws IOException {
        launchAction("selectByIdIn");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdIn.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdIn.xml",
            true);

    }


    public void testSelectByIdNotIn() throws IOException {
        launchAction("selectByIdNotIn");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdNotIn.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdNotIn.xml",
            true);

    }

    // List<Blog> selectByAgeBetween(@Param("beginAge") Integer beginAge, @Param("endAge") Integer endAge);
    public void testSelectByAgeBetween() throws IOException {
        launchAction("selectByAgeBetween");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeBetween.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeBetween.xml",
            true);

    }

    // List<Blog> selectByIdLike(@Param("id") Long id);
    public void testSelectByIdLike() throws IOException {
        launchAction("selectByIdLike");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdLike.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdLike.xml",
            true);

    }

    //     List<Blog> selectByIdNotLike(@Param("id") Long id);
    public void testSelectByIdNotLike() throws IOException {
        launchAction("selectByIdNotLike");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdNotLike.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdNotLike.xml",
            true);

    }

    //    List<Blog> selectByIdStartWith(@Param("id") Long id);
    public void testSelectByIdStartWith() throws IOException {
        launchAction("selectByIdStartWith");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByIdStartWith.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByIdStartWith.xml",
            true);

    }

    //     List<Blog> selectByTitleEndWith(@Param("title") String title);
    public void testSelectByTitleEndWith() throws IOException {
        launchAction("selectByTitleEndWith");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByTitleEndWith.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByTitleEndWith.xml",
            true);

    }

    //     List<Blog> selectByTitleContaining(@Param("title") String title);
    public void testSelectByTitleContaining() throws IOException {
        launchAction("selectByTitleContaining");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByTitleContaining.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByTitleContaining.xml",
            true);

    }

    //     List<Blog> selectByAgeTrue();
    public void testSelectByAgeTrue() throws IOException {
        launchAction("selectByAgeTrue");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeTrue.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeTrue.xml",
            true);

    }

    //     List<Blog> selectByAgeFalse();
    public void testSelectByAgeFalse() throws IOException {
        launchAction("selectByAgeFalse");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeFalse.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeFalse.xml",
            true);

    }

    //    List<Blog> selectByTitleIgnoreCase(@Param("title") String title);
    public void testSelectByTitleIgnoreCase() throws IOException {
        launchAction("selectByTitleIgnoreCase");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByTitleIgnoreCase.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByTitleIgnoreCase.xml",
            true);

    }

//    List<Blog> selectByAgeGreaterThan(@Param("age") Integer age);
    public void testSelectByAgeGreaterThan() throws IOException {
        launchAction("selectByAgeGreaterThan");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeGreaterThan.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeGreaterThan.xml",
            true);

    }


    // List<Blog> selectByAgeGreaterThanEqual(@Param("age") Integer age);
    public void testSelectByAgeGreaterThanEqual() throws IOException {
        launchAction("selectByAgeGreaterThanEqual");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeGreaterThanEqual.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeGreaterThanEqual.xml",
            true);

    }

  //   List<Blog> selectByAgeLessThan(@Param("age") Integer age);
    public void testSelectByAgeLessThan() throws IOException {
        launchAction("selectByAgeLessThan");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeLessThan.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeLessThan.xml",
            true);

    }

    //     List<Blog> selectByAgeLessThanEqual(@Param("age") Integer age);
    public void testSelectByAgeLessThanEqual() throws IOException {
        launchAction("selectByAgeLessThanEqual");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeLessThanEqual.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeLessThanEqual.xml",
            true);

    }



    // List<Blog> selectByAgeIs(@Param("age") Integer age);
    public void testSelectByAgeIs() throws IOException {
        launchAction("selectByAgeIs");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/select/SelectByAgeIs.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/select/SelectByAgeIs.xml",
            true);

    }




}
