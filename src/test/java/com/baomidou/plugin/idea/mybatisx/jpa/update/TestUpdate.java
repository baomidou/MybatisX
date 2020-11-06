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



}
