package com.baomidou.plugin.idea.mybatisx.generate.util;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 通过xml读取元数据配置
 */
public class XmlUtils {

    public static Map<String, TemplateSettingDTO> loadTemplatesByFile(InputStream inputStream) throws IOException {
        try {
            //1.或去SAXParserFactory实例
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //2.获取SAXParser实例
            SAXParser saxParser = factory.newSAXParser();
            //创建Handel对象
            SAXDemoHandel dh = new SAXDemoHandel();
            saxParser.parse(inputStream, dh);
            return dh.map;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IOException("读取配置文件出错", e);
        }
    }


    static class SAXDemoHandel extends DefaultHandler {
        Stack<Object> objects = new Stack<>();
        Map<String, TemplateSettingDTO> map = new HashMap<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equals("template")) {
                objects.push(new TemplateSettingDTO());
            } else if (qName.equals("property")) {
                TemplateSettingDTO peek = (TemplateSettingDTO) objects.peek();
                String name = attributes.getValue("name");
                String value = attributes.getValue("value");
                if (name.equals("configName")) {
                    peek.setConfigName(value);
                } else if (name.equals("configFile")) {
                    peek.setConfigFile(value);
                } else if (name.equals("fileName")) {
                    peek.setFileName(value);
                } else if (name.equals("suffix")) {
                    peek.setSuffix(value);
                } else if (name.equals("packageName")) {
                    peek.setPackageName(value);
                } else if (name.equals("encoding")) {
                    peek.setEncoding(value);
                } else if (name.equals("basePath")) {
                    peek.setBasePath(value);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equals("template")) {
                TemplateSettingDTO templateSettingDTO = (TemplateSettingDTO) objects.pop();
                map.put(templateSettingDTO.getConfigFile(), templateSettingDTO);
            }

        }

    }
}
