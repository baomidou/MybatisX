package com.baomidou.plugin.idea.mybatisx.generate;

import java.util.Collection;

/**
 * <p>
 * Model 代码生成器
 * </p>
 *
 * @author yanglin jobob
 * @since 2018 -07-30
 */
public abstract class GenerateModel {

    /**
     * The constant START_WITH_MODEL.
     */
    public static final GenerateModel START_WITH_MODEL = new StartWithModel();

    /**
     * The constant END_WITH_MODEL.
     */
    public static final GenerateModel END_WITH_MODEL = new EndWithModel();

    /**
     * The constant CONTAIN_MODEL.
     */
    public static final GenerateModel CONTAIN_MODEL = new ContainModel();

    /**
     * Gets instance.
     *
     * @param identifier the identifier
     * @return the instance
     */
    public static GenerateModel getInstance(String identifier) {
        try {
            return getInstance(Integer.valueOf(identifier));
        } catch (Exception e) {
            return START_WITH_MODEL;
        }
    }

    /**
     * Gets instance.
     *
     * @param identifier the identifier
     * @return the instance
     */
    public static GenerateModel getInstance(int identifier) {
        switch (identifier) {
            case 0:
                return START_WITH_MODEL;
            case 1:
                return END_WITH_MODEL;
            case 2:
                return CONTAIN_MODEL;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Matches any boolean.
     *
     * @param patterns the patterns
     * @param target   the target
     * @return the boolean
     */
    public boolean matchesAny(String[] patterns, String target) {
        for (String pattern : patterns) {
            if (apply(pattern, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Matches any boolean.
     *
     * @param patterns the patterns
     * @param target   the target
     * @return the boolean
     */
    public boolean matchesAny(Collection<String> patterns, String target) {
        return matchesAny(patterns.toArray(new String[patterns.size()]), target);
    }

    /**
     * Apply boolean.
     *
     * @param pattern the pattern
     * @param target  the target
     * @return the boolean
     */
    protected abstract boolean apply(String pattern, String target);

    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    public abstract int getIdentifier();

    /**
     * The type Start with model.
     */
    static class StartWithModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.startsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 0;
        }
    }

    /**
     * The type End with model.
     */
    static class EndWithModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.endsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 1;
        }
    }

    /**
     * The type Contain model.
     */
    static class ContainModel extends GenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.contains(pattern);
        }

        @Override
        public int getIdentifier() {
            return 2;
        }
    }
}
