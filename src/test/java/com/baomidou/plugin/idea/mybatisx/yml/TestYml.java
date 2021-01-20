package com.baomidou.plugin.idea.mybatisx.yml;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.regex.Pattern;

public class TestYml extends TestCase {

    private static class NodeIterable implements Iterable<Node> {
        private Iterator<Node> iterator;

        public NodeIterable(Iterator<Node> iterator) {
            this.iterator = iterator;
        }

        public Iterator<Node> iterator() {
            return this.iterator;
        }
    }


    /**
     * 用于测试 yml 中含有变量 @var@ 会导致载入失败的情况
     * <p>
     * 这种情况适用于 springboot 的配置
     */
    @Test(expected = ScannerException.class)
    public void testYmlRead() throws IOException {

        extracted();

    }

    private void extracted() throws IOException {

        String content = null;
        try (InputStream resourceAsStream = TestYml.class.getResourceAsStream("/yml/application-mybaits-plus3.yml")) {
            content = IOUtils.toString(resourceAsStream, "UTF-8");
            Yaml yaml = new Yaml();
            final Iterable<Object> objects = yaml.loadAll(content);

            for (Object object : objects) {
                System.out.println(object);
            }
            System.out.println("读取文件成功");
        } catch (ScannerException ignore) {
            // ignore
            extractedReplaced(content.replaceAll("@.*@", "empty"));
        }
    }

    private void extractedReplaced(String content) throws IOException {
        Yaml yaml = new Yaml();

        final Iterable<Object> objects = yaml.loadAll(content);

        for (Object object : objects) {
            System.out.println(object);
        }
        System.out.println("读取文件成功");
    }


}
