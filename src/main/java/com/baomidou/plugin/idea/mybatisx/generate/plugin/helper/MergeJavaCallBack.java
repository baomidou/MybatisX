package com.baomidou.plugin.idea.mybatisx.generate.plugin.helper;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MergeJavaCallBack extends DefaultShellCallback {

    public MergeJavaCallBack(boolean overwrite) {
        super(overwrite);
    }

    public static String readFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = "";
            while ((tempString = reader.readLine()) != null) {
                builder.append(tempString + "\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return builder.toString();
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        String result = newFileSource;

        String existingFileFullPath = existingFile.getAbsolutePath();
        if (isClassInterface(existingFileFullPath)) {
            result = readFile(existingFileFullPath);
        }
        return result;
    }

    private boolean isClassInterface(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        boolean result = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = "";
            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("interface") || tempString.contains("Impl")) {
                    result = true;
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }
}
