package com.baomidou.plugin.idea.mybatisx.util;

import org.mybatis.generator.api.ProgressCallback;

/**
 * mybatis generator进度回调
 * Created by kangtian on 2018/7/17.
 */
public class GeneratorCallback implements ProgressCallback {
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
        System.out.println("startTask" + s);
    }

    @Override
    public void done() {
    }

    @Override
    public void checkCancel() throws InterruptedException {

    }
}
