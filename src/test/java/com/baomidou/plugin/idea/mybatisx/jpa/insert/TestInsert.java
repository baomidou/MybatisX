package com.baomidou.plugin.idea.mybatisx.jpa.insert;

import com.baomidou.plugin.idea.mybatisx.jpa.BaseJpaTest;
import com.intellij.database.util.SqlDialects;
import com.intellij.sql.dialects.SqlDialectMappings;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.dialects.oracle.OracleDialect;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
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
public class TestInsert extends BaseJpaTest {

    private static final Logger logger = LoggerFactory.getLogger(TestInsert.class);


    public void testInsertSelective() throws IOException {
        launchAction("insertSelective");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/insert/InsertSelective.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/insert/InsertSelective.xml",
            true);

    }
//    int insertBatch(@Param("blogCollection") Collection<Blog> blogCollection);
    public void testInsertBatch() throws IOException {
        launchAction("insertBatch");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/insert/InsertBatch.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/insert/InsertBatch.xml",
            true);

    }

    // default insert all
    //   int insertAll(Blog blog);
    public void testInsertAll() throws IOException {
        launchAction("insertAll");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/insert/InsertAll.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/insert/InsertAll.xml",
            true);

    }

    //   TODO     oracle 的批量插入
    //    int insertBatchWithUnion(@Param("blogCollection") Collection<Blog> blogCollection);
    public void testInsertBatchWithUnion() throws IOException {
        SqlLanguageDialect defaultSqlDialect = SqlDialectMappings.getDefaultSqlDialect();
        SqlDialectMappings.setDefaultSqlDialect(OracleDialect.INSTANCE);

        launchAction("insertBatchWithUnion");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/insert/InsertBatchWithUnion.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/insert/InsertBatchWithUnion.xml",
            true);

        SqlDialectMappings.setDefaultSqlDialect(defaultSqlDialect);
    }


    // TODO     oracle 的批量插入
//    int insertBatchWithAll(@Param("blogCollection")Collection<Blog> blogCollection);
    public void testInsertBatchWithAll() throws IOException {
        SqlLanguageDialect defaultSqlDialect = SqlDialectMappings.getDefaultSqlDialect();
        SqlDialectMappings.setDefaultSqlDialect(OracleDialect.INSTANCE);

        launchAction("insertBatchWithAll");

        myFixture.checkResultByFile("template/TipMapper.java",
            "after/insert/InsertBatchWithAll.java",
            true);

        myFixture.checkResultByFile("template/TipMapper.xml",
            "after/insert/InsertBatchWithAll.xml",
            true);

        SqlDialectMappings.setDefaultSqlDialect(defaultSqlDialect);
    }


}
