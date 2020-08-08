package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;

public enum AreaSequence {
    UN_KNOWN(-100),
    RESULT(10),
    CONDITION(20),
    SORT(30),
    AREA(100);
    /**
     * 优先级
     */
    private int sequence;

    public int getSequence() {
        return this.sequence;
    }

    AreaSequence(int sequence) {

        this.sequence = sequence;
    }
}
