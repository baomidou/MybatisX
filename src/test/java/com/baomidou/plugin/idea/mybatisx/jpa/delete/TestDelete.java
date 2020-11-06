package com.baomidou.plugin.idea.mybatisx.jpa.delete;

import com.baomidou.plugin.idea.mybatisx.jpa.BaseJpaTest;
import com.intellij.database.util.SqlDialects;
import com.intellij.sql.dialects.SqlDialectMappings;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.dialects.oracle.OracleDialect;
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
public class TestDelete extends BaseJpaTest {

    private static final Logger logger = LoggerFactory.getLogger(TestDelete.class);


    public void testDeleteById() throws IOException {
        launchAction("deleteById");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/delete/DeleteById.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/delete/DeleteById.xml",
            true);

    }



}
