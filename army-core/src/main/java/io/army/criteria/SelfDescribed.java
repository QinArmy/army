package io.army.criteria;

public interface SelfDescribed {

    /**
     * This method has below step:
     * <ol>
     *     <li>append a space</li>
     *     <li>append SelfDescribed instance content in sql</li>
     * </ol>
     */
    void appendSQL(_SqlContext context);

}
