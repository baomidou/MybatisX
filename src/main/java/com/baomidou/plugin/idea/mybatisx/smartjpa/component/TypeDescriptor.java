package com.baomidou.plugin.idea.mybatisx.smartjpa.component;

import java.util.List;

public interface TypeDescriptor {
    /**
     * 导入的内容
     *
     * @return
     */
    List<String> getImportList();

    /**
     * 实际的内容
     *
     * @return
     */
    String getContent();
}
