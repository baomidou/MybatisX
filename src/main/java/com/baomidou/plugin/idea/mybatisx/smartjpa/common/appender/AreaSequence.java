package com.baomidou.plugin.idea.mybatisx.smartjpa.common.appender;

/**
 * The enum Area sequence.
 */
public enum AreaSequence {
    /**
     * Un known area sequence.
     */
    UN_KNOWN(-100),
    /**
     * Result area sequence.
     */
    RESULT(10),
    /**
     * Condition area sequence.
     */
    CONDITION(20),
    /**
     * Sort area sequence.
     */
    SORT(30),
    /**
     * Area area sequence.
     */
    AREA(100);
    /**
     * 优先级
     */
    private int sequence;

    AreaSequence(int sequence) {

        this.sequence = sequence;
    }

    /**
     * Gets sequence.
     *
     * @return the sequence
     */
    public int getSequence() {
        return this.sequence;
    }
}
