package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

public interface Generator {
    void generateSelect(String id, String value, String resultMap, String resultSet);

    void generateDelete(String id, String value);

    void generateInsert(String id, String value);

    void generateUpdate(String id, String value);
}
