package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import java.util.List;

/**
 * The interface Type descriptor.
 */
public interface TypeDescriptor {
    /**
     * 导入的内容
     *
     * @return import list
     */
    List<String> getImportList();

    /**
     * 实际的内容
     *
     * @return content
     * @param defaultDateList
     */
    String getContent(List<String> defaultDateList);
}
