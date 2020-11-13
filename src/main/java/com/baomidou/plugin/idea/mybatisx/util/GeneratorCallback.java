package com.baomidou.plugin.idea.mybatisx.util;

import org.mybatis.generator.api.ProgressCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mybatis generator进度回调
 * Created by kangtian on 2018/7/17.
 */
public class GeneratorCallback implements ProgressCallback {


    private static final Logger logger = LoggerFactory.getLogger(GeneratorCallback.class);

    @Override
    public void introspectionStarted(int i) {
    }

    @Override
    public void generationStarted(int i) {

    }

    @Override
    public void saveStarted(int i) {

    }


    @Override
    public void startTask(String s) {
        logger.info("startTask" + s);
    }

    @Override
    public void done() {
    }

    @Override
    public void checkCancel() throws InterruptedException {

    }
}
