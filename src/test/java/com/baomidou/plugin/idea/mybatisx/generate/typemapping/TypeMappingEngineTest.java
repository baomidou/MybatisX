package com.baomidou.plugin.idea.mybatisx.generate.typemapping;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TypeMappingEngineTest {
    private final TypeMappingEngine engine = new TypeMappingEngine();
    private final TypeMapping mysql = load("mysql.json");
    private final TypeMapping postgresql = load("postgresql.json");
    private final TypeMapping oracle = load("oracle.json");

    @Test
    public void mysqlIntegersFollowSignedAndUnsignedRange() {
        Assert.assertEquals("int", javaType(mysql, "int(11)", false));
        Assert.assertEquals("java.lang.Integer", javaType(mysql, "int(11)", true));
        Assert.assertEquals("java.lang.Long", javaType(mysql, "int(11) unsigned", false));
        Assert.assertEquals("java.lang.Long", javaType(mysql, "unsigned int(11)", true));
        Assert.assertEquals("java.lang.Long", javaType(mysql, "int(11) unsigned zerofill", false));
        Assert.assertEquals("INTEGER", jdbcType(mysql, "int unsigned"));
        Assert.assertEquals("java.math.BigInteger", javaType(mysql, "serial", false));
        Assert.assertEquals("java.lang.Integer", javaType(mysql, "tinyint(1)", false));
    }

    @Test
    public void mysqlDecimalUsesScaleAndPrecision() {
        Assert.assertEquals("java.lang.Long", javaType(mysql, "decimal(10,0)", false));
        Assert.assertEquals("java.math.BigDecimal", javaType(mysql, "decimal(10,2)", false));
        Assert.assertEquals("java.lang.Integer", javaType(mysql, "decimal(9,0)", false));
        Assert.assertEquals("java.math.BigDecimal", javaType(mysql, "decimal(19,0)", false));
    }

    @Test
    public void mysqlDatesFollowJsr310Switch() {
        Assert.assertEquals("java.time.LocalDate", javaType(mysql, "date", false));
        Assert.assertEquals("java.util.Date", javaType(mysql, "date", false, false));
        Assert.assertEquals("java.sql.Timestamp", javaType(mysql, "timestamp", false, false));
    }

    @Test
    public void postgresqlUsesItsOwnIntegerAndTimeNames() {
        Assert.assertEquals("java.lang.Integer", javaType(postgresql, "int4", false));
        Assert.assertEquals("java.time.LocalDateTime", javaType(postgresql, "timestamptz", false));
        Assert.assertEquals("java.lang.Long", javaType(postgresql, "oid", false));
    }

    @Test
    public void oracleNumberAndDateIncludeTime() {
        Assert.assertEquals("java.lang.Long", javaType(oracle, "number(10,0)", false));
        Assert.assertEquals("java.math.BigDecimal", javaType(oracle, "integer", false));
        Assert.assertEquals("java.time.LocalDateTime", javaType(oracle, "date", false));
        Assert.assertEquals("TIMESTAMP", jdbcType(oracle, "date"));
    }

    @Test
    public void invalidJsonIsSkipped() {
        Assert.assertTrue(TypeMappingLoader.parse("{").isEmpty());
        Assert.assertTrue(TypeMappingLoader.parse("{\"name\":\"x\"}").isEmpty());
    }

    @Test
    public void selectionRemembersFileAndFallsBackToMysql() {
        List<TypeMappingOption> options = List.of(
            new TypeMappingOption("oracle.json", "Oracle"),
            new TypeMappingOption("mysql.json", "MySQL"),
            new TypeMappingOption("custom.json", "Custom")
        );
        Assert.assertEquals("mysql.json", TypeMappingSelection.choose(options, null).getFileName());
        Assert.assertEquals("custom.json", TypeMappingSelection.choose(options, "custom.json").getFileName());
        Assert.assertEquals("mysql.json", TypeMappingSelection.choose(options, "missing.json").getFileName());
        Assert.assertEquals("oracle.json", TypeMappingSelection.choose(
            List.of(new TypeMappingOption("oracle.json", "Oracle")), "missing.json").getFileName());
    }

    private String javaType(TypeMapping mapping, String rawType, boolean nullable) {
        return javaType(mapping, rawType, nullable, true);
    }

    private String javaType(TypeMapping mapping, String rawType, boolean nullable, boolean jsr310) {
        return engine.match(mapping, rawType, null, null, nullable, jsr310)
            .orElseThrow(() -> new AssertionError(rawType))
            .javaType();
    }

    private String jdbcType(TypeMapping mapping, String rawType) {
        return engine.match(mapping, rawType, null, null, false, true)
            .orElseThrow(() -> new AssertionError(rawType))
            .jdbcType();
    }

    private static TypeMapping load(String fileName) {
        String path = "/extensions/type-mapping/" + fileName;
        try (InputStream input = TypeMappingEngineTest.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("缺少映射文件: " + path);
            }
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return TypeMappingLoader.parse(json).orElseThrow(() -> new IllegalStateException("无法解析: " + fileName));
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
