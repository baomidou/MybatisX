package com.baomidou.plugin.idea.mybatisx.template;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.baomidou.plugin.idea.mybatisx.generate.util.XmlUtils;
import com.intellij.testFramework.UsefulTestCase;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class XmlReaderTest extends UsefulTestCase {

    public void testDeleteById() throws IOException {

        URL resource = XmlReaderTest.class.getResource("/template/.meta.xml");
        try (InputStream inputStream = resource.openStream()) {
            Map<String, TemplateSettingDTO> map = XmlUtils.loadTemplatesByFile(inputStream);
            Assert.assertNotNull(map);
            Assert.assertEquals(map.size(), 4);
        }

    }


}
