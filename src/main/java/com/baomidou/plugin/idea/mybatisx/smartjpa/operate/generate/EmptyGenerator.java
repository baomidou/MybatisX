package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyGenerator implements Generator {
    private Logger logger = LoggerFactory.getLogger(EmptyGenerator.class);
    @Override
    public void generateSelect(String id, String value, String resultMap, String resultSet) {
        logger.warn("generateSelect fail");
    }

    @Override
    public void generateDelete(String id, String value) {
        logger.warn("generateDelete fail");
    }

    @Override
    public void generateInsert(String id, String value) {
        logger.warn("generateInsert fail");
    }

    @Override
    public void generateUpdate(String id, String value) {
        logger.warn("generateUpdate fail");
    }
}
