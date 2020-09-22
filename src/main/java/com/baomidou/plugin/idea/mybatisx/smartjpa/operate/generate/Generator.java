package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

/**
 * The interface Generator.
 */
public interface Generator {
    /**
     * Generate select.
     *
     * @param id        the id
     * @param value     the value
     * @param resultMap the result map
     * @param resultSet the result set
     */
    void generateSelect(String id, String value, String resultMap, String resultSet);

    /**
     * Generate delete.
     *
     * @param id    the id
     * @param value the value
     */
    void generateDelete(String id, String value);

    /**
     * Generate insert.
     *
     * @param id    the id
     * @param value the value
     */
    void generateInsert(String id, String value);

    /**
     * Generate update.
     *
     * @param id    the id
     * @param value the value
     */
    void generateUpdate(String id, String value);
}
