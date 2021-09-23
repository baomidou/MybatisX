package com.baomidou.plugin.idea.mybatisx.jpa.update;

import com.baomidou.plugin.idea.mybatisx.jpa.BaseJpaTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author ls9527
 * <p>
 * JavaCompletionTestCase
 * <p>
 * JavaCodeInsightFixtureTestCase
 */
public class TestUpdate extends BaseJpaTest {

    private static final Logger logger = LoggerFactory.getLogger(TestUpdate.class);


    public void testUpdateContentById() throws IOException {
        launchAction("updateContentById");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateContentById.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateContentById.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdIn() throws IOException {
        launchAction("updateAgeAndContentByIdIn");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdIn.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdIn.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdNotIn() throws IOException {
        launchAction("updateAgeAndContentByIdNotIn");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdNotIn.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdNotIn.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdBetween() throws IOException {
        launchAction("updateAgeAndContentByIdBetween");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdBetween.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdBetween.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdLike() throws IOException {
        launchAction("updateAgeAndContentByIdLike");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdLike.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdLike.xml",
            true);

    }



    public void testUpdateAgeAndContentByTitleNotLike() throws IOException {
        launchAction("updateAgeAndContentByTitleNotLike");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByTitleNotLike.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByTitleNotLike.xml",
            true);

    }



    public void testUpdateAgeAndContentByTitleStartWith() throws IOException {
        launchAction("updateAgeAndContentByTitleStartWith");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByTitleStartWith.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByTitleStartWith.xml",
            true);

    }



    public void testUpdateAgeAndContentByTitleEndWith() throws IOException {
        launchAction("updateAgeAndContentByTitleEndWith");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByTitleEndWith.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByTitleEndWith.xml",
            true);

    }



    public void testUpdateAgeAndContentByTitleContaining() throws IOException {
        launchAction("updateAgeAndContentByTitleContaining");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByTitleContaining.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByTitleContaining.xml",
            true);

    }



    public void testUpdateAgeAndContentByTitleIgnoreCase() throws IOException {
        launchAction("updateAgeAndContentByTitleIgnoreCase");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByTitleIgnoreCase.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByTitleIgnoreCase.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdGreaterThan() throws IOException {
        launchAction("updateAgeAndContentByIdGreaterThan");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdGreaterThan.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdGreaterThan.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdGreaterThanEqual() throws IOException {
        launchAction("updateAgeAndContentByIdGreaterThanEqual");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdGreaterThanEqual.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdGreaterThanEqual.xml",
            true);

    }



    public void testUpdateAgeAndContentByIdIs() throws IOException {
        launchAction("updateAgeAndContentByIdIs");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByIdIs.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByIdIs.xml",
            true);

    }


    public void testUpdateAgeByAgeAfter() throws IOException {
        launchAction("updateAgeByAgeAfter");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeByAgeAfter.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeByAgeAfter.xml",
            true);

    }
    public void testUpdateAgeAndContentByAgeTrue() throws IOException {
        launchAction("updateAgeAndContentByAgeTrue");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByAgeTrue.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByAgeTrue.xml",
            true);

    }

    public void testUpdateAgeAndContentByAgeFalse() throws IOException {
        launchAction("updateAgeAndContentByAgeFalse");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateAgeAndContentByAgeFalse.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateAgeAndContentByAgeFalse.xml",
            true);

    }

    public void testUpdateSelective() throws IOException {
        launchAction("updateSelective");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/update/UpdateSelective.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/update/UpdateSelective.xml",
            true);

    }
}
